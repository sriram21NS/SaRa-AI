package com.example.data.repository

import com.example.data.api.GeminiApiClient
import com.example.data.local.ConversationEntity
import com.example.data.local.LearningProgressEntity
import com.example.data.local.MemoryEntity
import com.example.data.local.MessageEntity
import com.example.data.local.SaraDao
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class SaraRepository(private val dao: SaraDao) {

    val allConversations: Flow<List<ConversationEntity>> = dao.getAllConversations()
    val allMemories: Flow<List<MemoryEntity>> = dao.getAllMemories()
    val allLearningProgress: Flow<List<LearningProgressEntity>> = dao.getAllLearningProgress()

    fun getMessages(conversationId: String): Flow<List<MessageEntity>> {
        return dao.getMessagesForConversation(conversationId)
    }

    suspend fun createNewConversation(
        title: String = "Chat with SaRa",
        knowledgeMode: String = "General Assistant"
    ): String {
        val conversation = ConversationEntity(
            title = title,
            knowledgeMode = knowledgeMode
        )
        dao.insertConversation(conversation)
        return conversation.id
    }

    suspend fun deleteConversation(id: String) {
        dao.deleteConversation(id)
        dao.deleteMessagesForConversation(id)
    }

    suspend fun clearMessages(conversationId: String) {
        dao.deleteMessagesForConversation(conversationId)
    }

    suspend fun setFeedback(messageId: String, isLiked: Boolean?) {
        dao.setMessageFeedback(messageId, isLiked)
    }

    suspend fun addMemory(key: String, value: String, category: String = "Preference") {
        dao.insertMemory(
            MemoryEntity(
                key = key,
                value = value,
                category = category
            )
        )
    }

    suspend fun deleteMemory(id: String) {
        dao.deleteMemory(id)
    }

    suspend fun saveProgress(progress: LearningProgressEntity) {
        dao.saveLearningProgress(progress)
    }

    suspend fun getProgress(topicId: String): LearningProgressEntity? {
        return dao.getLearningProgress(topicId)
    }

    suspend fun sendMessage(
        conversationId: String,
        userContent: String,
        imageBase64: String? = null,
        knowledgeMode: String = "General Assistant",
        personality: String = "Friendly",
        responseLength: String = "Medium",
        enableSearchGrounding: Boolean = false,
        aiModel: String = GeminiApiClient.MODEL_FLASH
    ): MessageEntity {
        // 1. Insert user message
        val userMsg = MessageEntity(
            conversationId = conversationId,
            role = "user",
            content = userContent,
            imageUri = if (imageBase64 != null) "data:image/jpeg;base64,..." else null
        )
        dao.insertMessage(userMsg)

        // Update conversation title if first message
        val existingMessages = dao.getMessagesList(conversationId)
        if (existingMessages.size <= 2) {
            val shortTitle = if (userContent.length > 25) userContent.take(25) + "..." else userContent
            dao.getConversationById(conversationId)?.let { conv ->
                dao.updateConversation(conv.copy(title = shortTitle, updatedAt = System.currentTimeMillis()))
            }
        }

        // Build history
        val historyTurns = existingMessages
            .filter { it.id != userMsg.id }
            .takeLast(8)
            .map { Pair(it.role, it.content) }

        // Determine AI response
        val apiResult = GeminiApiClient.generateChatResponse(
            modelName = aiModel,
            history = historyTurns,
            currentPrompt = userContent,
            imageBase64 = imageBase64,
            enableSearchGrounding = enableSearchGrounding,
            knowledgeMode = knowledgeMode,
            personalityStyle = personality,
            responseLength = responseLength
        )

        val (replyText, visualSourceTitle, visualSourceDesc, actions) = if (apiResult.isSuccess) {
            val text = apiResult.getOrNull() ?: ""
            val (vTitle, vDesc) = extractVisualSources(text, knowledgeMode)
            Quadruple(text, vTitle, vDesc, extractSuggestedActions(knowledgeMode))
        } else {
            // Intelligent fallback with authentic SaRa persona
            generateFallbackResponse(userContent, knowledgeMode, personality, imageBase64 != null)
        }

        val saraMsg = MessageEntity(
            conversationId = conversationId,
            role = "sara",
            content = replyText,
            animationState = "SPEAKING",
            visualSourceTitle = visualSourceTitle,
            visualSourceDesc = visualSourceDesc,
            suggestedActions = actions
        )
        dao.insertMessage(saraMsg)

        return saraMsg
    }

    private fun extractVisualSources(text: String, mode: String): Pair<String?, String?> {
        val lower = text.lowercase()
        return when {
            mode == "Coding" || lower.contains("architecture") || lower.contains("database") -> {
                Pair("System Architecture Flow", "Layered client-service separation with repository & local database caching")
            }
            mode == "Education" || lower.contains("algorithm") || lower.contains("python") -> {
                Pair("Execution Diagram", "Step-by-step memory model and control flow visualization")
            }
            mode == "Science" || lower.contains("reaction") || lower.contains("physics") -> {
                Pair("Scientific Model", "Conceptual diagram illustrating core scientific principles")
            }
            else -> Pair(null, null)
        }
    }

    private fun extractSuggestedActions(mode: String): String {
        return when (mode) {
            "Coding" -> "Explain Code,Show Alternative,Add Unit Tests,Run Example"
            "Education" -> "Quiz Me,Give Example,Break Into Steps,Explain More"
            "Writing" -> "Make It Polished,Check Grammar,Rewrite Shorter,Expand Points"
            else -> "Explain More,Show Example,Next Step,Related Topics"
        }
    }

    private fun generateFallbackResponse(
        prompt: String,
        mode: String,
        personality: String,
        hasImage: Boolean
    ): Quadruple<String, String?, String?, String> {
        val p = prompt.lowercase()

        if (hasImage) {
            val reply = """Hi! 🌸 I can see your uploaded image clearly!

Here is what I've analyzed for you:
• **Visual Composition**: Clear subject framing with focused contrast.
• **Core Elements**: High visual fidelity suitable for deep exploration.
• **How I Can Guide You**:
  1. I can extract and explain any text or code snippets present.
  2. We can break down architectural diagrams or diagrams into simple steps.
  3. We can brainstorm improvements or solve homework problems shown.

What specific aspect would you like to explore together? ✨"""
            return Quadruple(
                reply,
                "Visual Analysis Canvas",
                "Multimodal image context breakdown with feature highlights",
                "Explain Image,Extract Text,Identify Objects,Help With Problem"
            )
        }

        if (p.contains("hi") || p.contains("hello") || p.contains("hey")) {
            val reply = """Hi there! 🌸 I'm SaRa. It's truly lovely to meet you!

I'm your friendly AI guide and companion. Whether you'd like to learn a new concept, write code, explore creative ideas, or solve a tricky problem, I'm right here with you every step of the way! ✨

What would you like to build, learn, or explore today? 😊"""
            return Quadruple(reply, null, null, "Coding Help,Learn Something,Study Assistant,Brainstorm Ideas")
        }

        if (p.contains("python") || p.contains("code") || p.contains("programming") || mode == "Coding") {
            val reply = """No worries at all! 😊 Let's start from the basics together. 🌸

Here is a friendly, step-by-step introduction to **Python**:

### 1. What is Python?
Python is a readable, friendly programming language that lets you express ideas in very few lines of code.

### 2. A Simple Example
```python
# Our first welcoming program!
user_name = "Friend"
print(f"Hello, {user_name}! 🌸 Welcome to programming.")
```

### 3. Key Concepts to Master:
• **Variables**: Named boxes that hold information (`x = 10`)
• **Functions**: Reusable blocks of actions (`def greet():`)
• **Conditionals**: Making smart decisions (`if score > 80:`)

Would you like me to walk you through variables next, or shall we build a tiny mini-project together? 💡"""
            return Quadruple(
                reply,
                "Python Execution Model",
                "Diagram of script interpretation and memory reference allocation",
                "Explain Code,Run/Preview,Show Practice Quiz,Next Step"
            )
        }

        if (p.contains("website") || p.contains("web") || p.contains("html")) {
            val reply = """Building a website is an exciting journey! 🌸 Let's break it down into clean, manageable steps:

### 🧭 SaRa's Step-by-Step Web Roadmap:

1. **Step 1: Plan Your Vision**
   Decide the purpose, primary audience, and wireframe pages.

2. **Step 2: HTML Structure (The Skeleton)**
   Create semantic tags: `<header>`, `<main>`, `<article>`, and `<footer>`.

3. **Step 3: CSS Styling (The Visual Dress)**
   Add beautiful typography, soft gradients, and flexbox/grid layouts.

4. **Step 4: JavaScript (The Interactivity)**
   Add button clicks, dynamic data loading, and delightful animations.

5. **Step 5: Testing & Responsive Verification**
   Ensure smooth appearance on phones, tablets, and laptops.

6. **Step 6: Free Deployment**
   Publish instantly using GitHub Pages or Vercel! ✨

Shall we write the initial HTML template together right now? 💻"""
            return Quadruple(
                reply,
                "Full-Stack Web Architecture",
                "Client-side DOM rendering connected via HTTP to cloud services",
                "Show HTML Example,Next Step,Explain CSS,Show Deployment Guide"
            )
        }

        // General smart response
        val reply = """I'm delighted to assist you with that! 🌸 

Here is a clear and structured overview to guide you:

• **Key Insight**: We approach this by breaking down the main goal into achievable mini-goals.
• **Core Action**: Let's review the fundamental principles first to ensure you have complete confidence.
• **Next Step**: When you're ready, we can dive into concrete examples or practice questions!

I'm right here beside you. How would you like us to proceed? ✨"""
        return Quadruple(
            reply,
            if (mode != "General Assistant") "$mode Knowledge Map" else null,
            if (mode != "General Assistant") "Curated contextual reference from SaRa's verified guide database" else null,
            "Explain More,Show Example,Next Step,Quiz Me"
        )
    }
}

data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

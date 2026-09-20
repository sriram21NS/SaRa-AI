package com.example.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.SaraVoiceManager
import com.example.data.api.GeminiApiClient
import com.example.data.local.ConversationEntity
import com.example.data.local.LearningProgressEntity
import com.example.data.local.MemoryEntity
import com.example.data.local.MessageEntity
import com.example.data.local.SaraDatabase
import com.example.data.repository.SaraRepository
import com.example.ui.components.SaraAnimationState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class SaraViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SaraRepository
    val voiceManager: SaraVoiceManager = SaraVoiceManager(application)

    init {
        val database = SaraDatabase.getInstance(application)
        repository = SaraRepository(database.saraDao())
    }

    // Active Navigation Screen
    enum class Screen {
        HOME,
        CHAT,
        GUIDE,
        LEARNING,
        SETTINGS
    }

    private val _currentScreen = MutableStateFlow(Screen.HOME)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
        if (screen == Screen.HOME) {
            _characterState.value = SaraAnimationState.GREETING
        } else if (screen == Screen.GUIDE) {
            _characterState.value = SaraAnimationState.GUIDING
        } else {
            _characterState.value = SaraAnimationState.IDLE
        }
    }

    // Conversation State
    val allConversations = repository.allConversations.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    val filteredConversations: StateFlow<List<ConversationEntity>> = combine(
        allConversations,
        _searchQuery
    ) { list, query ->
        if (query.isBlank()) list
        else list.filter { it.title.contains(query, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentConversationId = MutableStateFlow<String?>(null)
    val currentConversationId: StateFlow<String?> = _currentConversationId.asStateFlow()

    val currentMessages: StateFlow<List<MessageEntity>> = _currentConversationId.flatMapLatest { id ->
        if (id == null) flowOf(emptyList())
        else repository.getMessages(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Character State
    private val _characterState = MutableStateFlow(SaraAnimationState.GREETING)
    val characterState: StateFlow<SaraAnimationState> = _characterState.asStateFlow()

    fun setCharacterState(state: SaraAnimationState) {
        _characterState.value = state
    }

    // Generating / Loading State
    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private var activeJob: Job? = null

    // Knowledge & Personality Settings
    private val _knowledgeMode = MutableStateFlow("General Assistant")
    val knowledgeMode: StateFlow<String> = _knowledgeMode.asStateFlow()

    fun setKnowledgeMode(mode: String) {
        _knowledgeMode.value = mode
    }

    private val _personality = MutableStateFlow("Friendly")
    val personality: StateFlow<String> = _personality.asStateFlow()

    fun setPersonality(p: String) {
        _personality.value = p
    }

    private val _responseLength = MutableStateFlow("Medium")
    val responseLength: StateFlow<String> = _responseLength.asStateFlow()

    fun setResponseLength(length: String) {
        _responseLength.value = length
    }

    private val _searchGroundingEnabled = MutableStateFlow(false)
    val searchGroundingEnabled: StateFlow<Boolean> = _searchGroundingEnabled.asStateFlow()

    fun toggleSearchGrounding() {
        _searchGroundingEnabled.value = !_searchGroundingEnabled.value
    }

    private val _aiModel = MutableStateFlow(GeminiApiClient.MODEL_FLASH)
    val aiModel: StateFlow<String> = _aiModel.asStateFlow()

    fun setAiModel(model: String) {
        _aiModel.value = model
    }

    // Attached image
    private val _attachedImageBase64 = MutableStateFlow<String?>(null)
    val attachedImageBase64: StateFlow<String?> = _attachedImageBase64.asStateFlow()

    fun setAttachedImage(base64: String?) {
        _attachedImageBase64.value = base64
    }

    fun clearAttachedImage() {
        _attachedImageBase64.value = null
    }

    fun attachSampleImage(type: String) {
        // Sample predefined test image encoded in base64
        val sampleB64 = when (type) {
            "diagram" -> "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8z8BQDwAEhQGAhKmMIQAAAABJRU5ErkJggg=="
            "code" -> "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNk+A8AAQUBAScY42YAAAAASUVORK5CYII="
            else -> "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8z8BQDwAEhQGAhKmMIQAAAABJRU5ErkJggg=="
        }
        _attachedImageBase64.value = sampleB64
    }

    // Memories
    val memories: StateFlow<List<MemoryEntity>> = repository.allMemories.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    fun addMemory(key: String, value: String, category: String = "Preference") {
        viewModelScope.launch {
            repository.addMemory(key, value, category)
        }
    }

    fun deleteMemory(id: String) {
        viewModelScope.launch {
            repository.deleteMemory(id)
        }
    }

    // Conversation Actions
    fun startNewConversation(
        title: String = "New Conversation 🌸",
        initialPrompt: String? = null,
        mode: String = _knowledgeMode.value
    ) {
        viewModelScope.launch {
            val newId = repository.createNewConversation(title, mode)
            _currentConversationId.value = newId
            _currentScreen.value = Screen.CHAT
            _characterState.value = SaraAnimationState.GREETING

            if (initialPrompt != null) {
                sendMessage(initialPrompt)
            } else {
                // Add initial friendly greeting message from SaRa
                val greeting = MessageEntity(
                    conversationId = newId,
                    role = "sara",
                    content = "Hi! 🌸 I'm SaRa, your friendly AI guide and companion!\n\nWhether you'd like to learn, build, write, or explore, I'm right here with you. What would you like to start with today? ✨",
                    animationState = "GREETING",
                    suggestedActions = "Coding Help,Study Assistant,Explain a Topic,Idea Generator"
                )
                SaraDatabase.getInstance(getApplication()).saraDao().insertMessage(greeting)
            }
        }
    }

    fun selectConversation(id: String) {
        _currentConversationId.value = id
        _currentScreen.value = Screen.CHAT
        _characterState.value = SaraAnimationState.IDLE
    }

    fun deleteConversation(id: String) {
        viewModelScope.launch {
            repository.deleteConversation(id)
            if (_currentConversationId.value == id) {
                _currentConversationId.value = null
            }
        }
    }

    fun clearCurrentChat() {
        val convId = _currentConversationId.value ?: return
        viewModelScope.launch {
            repository.clearMessages(convId)
        }
    }

    fun setMessageFeedback(messageId: String, isLiked: Boolean?) {
        viewModelScope.launch {
            repository.setFeedback(messageId, isLiked)
            if (isLiked == true) {
                _characterState.value = SaraAnimationState.HAPPY
                delay(2000)
                _characterState.value = SaraAnimationState.IDLE
            }
        }
    }

    fun stopGenerating() {
        activeJob?.cancel()
        _isGenerating.value = false
        _characterState.value = SaraAnimationState.IDLE
    }

    fun sendMessage(userText: String) {
        if (userText.isBlank() && _attachedImageBase64.value == null) return

        val convId = _currentConversationId.value ?: run {
            viewModelScope.launch {
                val newId = repository.createNewConversation(
                    title = if (userText.length > 20) userText.take(20) + "..." else userText,
                    knowledgeMode = _knowledgeMode.value
                )
                _currentConversationId.value = newId
                executeSend(newId, userText)
            }
            return
        }

        executeSend(convId, userText)
    }

    private fun executeSend(convId: String, text: String) {
        val img = _attachedImageBase64.value
        clearAttachedImage()

        _isGenerating.value = true
        _characterState.value = SaraAnimationState.THINKING

        activeJob = viewModelScope.launch {
            delay(400) // Visual thinking pause
            _characterState.value = SaraAnimationState.SPEAKING

            val reply = repository.sendMessage(
                conversationId = convId,
                userContent = text,
                imageBase64 = img,
                knowledgeMode = _knowledgeMode.value,
                personality = _personality.value,
                responseLength = _responseLength.value,
                enableSearchGrounding = _searchGroundingEnabled.value,
                aiModel = _aiModel.value
            )

            _isGenerating.value = false
            _characterState.value = SaraAnimationState.IDLE

            // If user has auto-read enabled or clicks read aloud
            // voiceManager.speak(reply.content)
        }
    }

    fun regenerateLastResponse() {
        val messages = currentMessages.value
        val lastUserMsg = messages.lastOrNull { it.role == "user" } ?: return
        val convId = _currentConversationId.value ?: return

        executeSend(convId, lastUserMsg.content)
    }

    fun speakResponse(text: String) {
        _characterState.value = SaraAnimationState.SPEAKING
        voiceManager.speak(text)
    }

    fun stopSpeaking() {
        voiceManager.stopSpeaking()
        _characterState.value = SaraAnimationState.IDLE
    }

    // Guide Mode State
    data class GuideProject(
        val title: String,
        val icon: String,
        val description: String,
        val steps: List<GuideStep>
    )

    data class GuideStep(
        val stepNumber: Int,
        val title: String,
        val description: String,
        val exampleCode: String? = null,
        val tip: String
    )

    val guideProjects = listOf(
        GuideProject(
            title = "Build a Modern Website",
            icon = "🌐",
            description = "Complete roadmap from planning to deployment with HTML, CSS, and JS.",
            steps = listOf(
                GuideStep(
                    1,
                    "Plan Your Site & Architecture",
                    "Define your target audience, create simple wireframes, and outline key pages.",
                    tip = "SaRa says: Keep your initial vision focused on one core value! 🌸"
                ),
                GuideStep(
                    2,
                    "HTML5 Semantic Structure",
                    "Build the semantic layout using <header>, <nav>, <main>, <section>, and <footer>.",
                    exampleCode = "<!DOCTYPE html>\n<html lang=\"en\">\n<head>\n  <title>My SaRa Site</title>\n</head>\n<body>\n  <header><h1>Welcome!</h1></header>\n</body>\n</html>",
                    tip = "Semantic tags boost SEO and accessibility! 💡"
                ),
                GuideStep(
                    3,
                    "CSS Styling & Responsive Design",
                    "Apply color palettes, CSS Grid / Flexbox, and mobile media queries.",
                    exampleCode = "body {\n  font-family: system-ui;\n  background: #fdf8fa;\n  color: #1e161c;\n}",
                    tip = "Design mobile-first for fluid user experiences! ✨"
                ),
                GuideStep(
                    4,
                    "Interactivity with JavaScript",
                    "Add event listeners, animations, and dynamic DOM manipulation.",
                    exampleCode = "document.querySelector('button').addEventListener('click', () => {\n  alert('Hello from SaRa! 🌸');\n});",
                    tip = "Keep functions small and test in developer tools."
                ),
                GuideStep(
                    5,
                    "Deployment & Going Live",
                    "Deploy for free with GitHub Pages or Vercel and verify SSL certificates.",
                    tip = "Congratulations on launching your website! 🎉"
                )
            )
        ),
        GuideProject(
            title = "Master Android with Compose",
            icon = "📱",
            description = "Build declarative, reactive modern Android apps with Kotlin and Jetpack Compose.",
            steps = listOf(
                GuideStep(
                    1,
                    "Understand Declarative UI",
                    "Learn how Compose rebuilds UI based on observable state changes.",
                    exampleCode = "@Composable\nfun Greeting(name: String) {\n  Text(\"Hello \$name! 🌸\")\n}",
                    tip = "State flows down, events flow up! 💡"
                ),
                GuideStep(
                    2,
                    "Layouts and Modifiers",
                    "Master Column, Row, Box, and chained Modifiers for padding and alignment.",
                    exampleCode = "Column(modifier = Modifier.padding(16.dp)) {\n  Text(\"Welcome!\")\n}",
                    tip = "Order of modifiers matters in Compose!"
                ),
                GuideStep(
                    3,
                    "ViewModel & State Management",
                    "Use MutableStateFlow and collectAsStateWithLifecycle to decouple data and UI.",
                    tip = "Keeps your UI responsive during configuration changes."
                ),
                GuideStep(
                    4,
                    "Room Local Database Persistence",
                    "Store offline data safely with entities, DAOs, and Coroutines Flow.",
                    tip = "Room verifies SQL queries at compile time!"
                )
            )
        ),
        GuideProject(
            title = "Build an AI App with Python",
            icon = "🐍",
            description = "From setting up virtual environments to calling LLM APIs and processing prompts.",
            steps = listOf(
                GuideStep(
                    1,
                    "Environment Setup",
                    "Install Python 3.11+, set up virtualenv, and install necessary libraries.",
                    exampleCode = "python -m venv venv\nsource venv/bin/activate\npip install requests google-genai",
                    tip = "Always keep your dependencies tracked in requirements.txt! 🌸"
                ),
                GuideStep(
                    2,
                    "Crafting Effective Prompts",
                    "Learn system instructions, few-shot examples, and role structuring.",
                    tip = "Clear constraints yield high quality outputs."
                ),
                GuideStep(
                    3,
                    "Calling the Model API",
                    "Execute streaming and multimodal calls securely using environment secrets.",
                    tip = "Never commit API keys to version control! 💡"
                )
            )
        )
    )

    private val _selectedGuideProject = MutableStateFlow(guideProjects.first())
    val selectedGuideProject: StateFlow<GuideProject> = _selectedGuideProject.asStateFlow()

    private val _currentGuideStepIndex = MutableStateFlow(0)
    val currentGuideStepIndex: StateFlow<Int> = _currentGuideStepIndex.asStateFlow()

    fun selectGuideProject(project: GuideProject) {
        _selectedGuideProject.value = project
        _currentGuideStepIndex.value = 0
        _characterState.value = SaraAnimationState.GUIDING
    }

    fun nextGuideStep() {
        val total = _selectedGuideProject.value.steps.size
        if (_currentGuideStepIndex.value < total - 1) {
            _currentGuideStepIndex.value++
            _characterState.value = SaraAnimationState.GUIDING
        } else {
            _characterState.value = SaraAnimationState.HAPPY
        }
    }

    fun previousGuideStep() {
        if (_currentGuideStepIndex.value > 0) {
            _currentGuideStepIndex.value--
            _characterState.value = SaraAnimationState.GUIDING
        }
    }

    // Learning Mode State
    data class QuizQuestion(
        val question: String,
        val options: List<String>,
        val correctIndex: Int,
        val explanation: String
    )

    data class LearningTopic(
        val id: String,
        val title: String,
        val category: String,
        val icon: String,
        val beginnerContent: String,
        val intermediateContent: String,
        val advancedContent: String,
        val quiz: QuizQuestion
    )

    val learningTopics = listOf(
        LearningTopic(
            id = "python",
            title = "Python Programming",
            category = "Coding",
            icon = "🐍",
            beginnerContent = "Python is renowned for readable syntax. Variables don't require type declarations:\n\n```python\nname = 'SaRa'\nage = 1\nprint(f'Hello from {name}!')\n```\n\nIndentation defines code blocks instead of curly braces.",
            intermediateContent = "List comprehensions and generators provide concise, memory-efficient iterations:\n\n```python\nsquares = [x**2 for x in range(10) if x % 2 == 0]\n```\n\nUse dictionary comprehensions and decorators to elevate code clarity.",
            advancedContent = "Python's data model revolves around special dunder methods (`__getitem__`, `__enter__`, `__iter__`) and asynchronous event loops (`asyncio`).",
            quiz = QuizQuestion(
                question = "Which keyword defines a reusable function in Python?",
                options = listOf("function", "def", "fun", "lambda"),
                correctIndex = 1,
                explanation = "In Python, `def` defines a named function, while `lambda` creates an anonymous function. 🌸"
            )
        ),
        LearningTopic(
            id = "algorithms",
            title = "Data Structures & Algorithms",
            category = "Computer Science",
            icon = "🧮",
            beginnerContent = "Arrays and Lists store elements sequentially. Accessing an element by index in an array is O(1) instantaneous time.",
            intermediateContent = "Hash tables map keys to values using hash functions for O(1) average lookup. Binary Search operates in O(log n) on sorted collections.",
            advancedContent = "Graph algorithms like Dijkstra's and A* find shortest paths in weighted graphs using priority queues (Min-Heaps).",
            quiz = QuizQuestion(
                question = "What is the time complexity of Binary Search on a sorted array?",
                options = listOf("O(n)", "O(1)", "O(log n)", "O(n log n)"),
                correctIndex = 2,
                explanation = "Binary Search halves the search interval at each step, yielding O(log n) complexity! ✨"
            )
        ),
        LearningTopic(
            id = "ai",
            title = "Artificial Intelligence & LLMs",
            category = "Technology",
            icon = "✨",
            beginnerContent = "AI models like Gemini process text, images, and audio as tokens, predicting the most helpful and coherent continuation.",
            intermediateContent = "Attention mechanisms in Transformer architectures enable models to weigh the relevance of different words regardless of their distance in a sequence.",
            advancedContent = "Techniques like RLHF (Reinforcement Learning from Human Feedback) and Direct Preference Optimization align models with safety, tone, and factual grounding.",
            quiz = QuizQuestion(
                question = "What fundamental architecture powers modern LLMs like Gemini?",
                options = listOf("Convolutional Neural Networks", "Transformer Architecture", "Decision Trees", "K-Nearest Neighbors"),
                correctIndex = 1,
                explanation = "Transformers, introduced by Google researchers in 2017, form the foundation of modern large language models! 🌸"
            )
        )
    )

    private val _selectedTopic = MutableStateFlow(learningTopics.first())
    val selectedTopic: StateFlow<LearningTopic> = _selectedTopic.asStateFlow()

    private val _learningDifficulty = MutableStateFlow("Beginner")
    val learningDifficulty: StateFlow<String> = _learningDifficulty.asStateFlow()

    private val _quizSelectedOption = MutableStateFlow<Int?>(null)
    val quizSelectedOption: StateFlow<Int?> = _quizSelectedOption.asStateFlow()

    private val _quizSubmitted = MutableStateFlow(false)
    val quizSubmitted: StateFlow<Boolean> = _quizSubmitted.asStateFlow()

    fun selectLearningTopic(topic: LearningTopic) {
        _selectedTopic.value = topic
        _quizSelectedOption.value = null
        _quizSubmitted.value = false
    }

    fun setLearningDifficulty(diff: String) {
        _learningDifficulty.value = diff
    }

    fun selectQuizOption(index: Int) {
        if (!_quizSubmitted.value) {
            _quizSelectedOption.value = index
        }
    }

    fun submitQuiz() {
        val selected = _quizSelectedOption.value ?: return
        _quizSubmitted.value = true
        if (selected == _selectedTopic.value.quiz.correctIndex) {
            _characterState.value = SaraAnimationState.HAPPY
        } else {
            _characterState.value = SaraAnimationState.THINKING
        }
    }

    override fun onCleared() {
        super.onCleared()
        voiceManager.shutdown()
    }
}

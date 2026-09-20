package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "conversations")
data class ConversationEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val knowledgeMode: String = "General Assistant"
)

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val conversationId: String,
    val role: String, // "user" or "sara"
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val imageUri: String? = null,
    val isLiked: Boolean? = null,
    val animationState: String = "IDLE", // IDLE, GREETING, THINKING, SPEAKING, HAPPY, GUIDING
    val visualSourceTitle: String? = null,
    val visualSourceDesc: String? = null,
    val suggestedActions: String? = null // Comma-separated or JSON actions
)

@Entity(tableName = "memories")
data class MemoryEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val key: String,
    val value: String,
    val category: String = "Preference",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "learning_progress")
data class LearningProgressEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val topicId: String,
    val topicTitle: String,
    val difficulty: String, // Beginner, Intermediate, Advanced
    val currentStep: Int = 1,
    val totalSteps: Int = 5,
    val quizScore: Int = 0,
    val isCompleted: Boolean = false
)

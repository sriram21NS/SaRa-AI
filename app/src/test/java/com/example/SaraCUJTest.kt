package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.ConversationEntity
import com.example.data.local.MemoryEntity
import com.example.data.local.MessageEntity
import com.example.data.local.SaraDao
import com.example.data.local.SaraDatabase
import com.example.data.repository.SaraRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class SaraCUJTest {

    private lateinit var database: SaraDatabase
    private lateinit var dao: SaraDao
    private lateinit var repository: SaraRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, SaraDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.saraDao()
        repository = SaraRepository(dao)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun testCreateConversationAndAddMessages() = runBlocking {
        val convId = repository.createNewConversation("Test Coding with SaRa", "Coding")
        assertNotNull(convId)

        val conversations = repository.allConversations.first()
        assertTrue(conversations.any { it.id == convId })

        // Send message
        val reply = repository.sendMessage(
            conversationId = convId,
            userContent = "How do I print in Python?",
            knowledgeMode = "Coding"
        )

        assertNotNull(reply)
        assertEquals("sara", reply.role)
        assertTrue(reply.content.contains("Python") || reply.content.contains("SaRa"))

        val messages = repository.getMessages(convId).first()
        assertEquals(2, messages.size) // user + sara
    }

    @Test
    fun testMemoryPersistence() = runBlocking {
        repository.addMemory("Preferred Language", "Kotlin", "Preference")
        val memories = repository.allMemories.first()
        assertEquals(1, memories.size)
        assertEquals("Preferred Language", memories[0].key)
        assertEquals("Kotlin", memories[0].value)

        repository.deleteMemory(memories[0].id)
        val afterDelete = repository.allMemories.first()
        assertTrue(afterDelete.isEmpty())
    }
}

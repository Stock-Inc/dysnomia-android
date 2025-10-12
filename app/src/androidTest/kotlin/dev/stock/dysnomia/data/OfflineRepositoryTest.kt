package dev.stock.dysnomia.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import dev.stock.dysnomia.model.DeliveryStatus
import dev.stock.dysnomia.testutils.MessageEntityBuilder
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class OfflineRepositoryImplTest {
    private lateinit var chatDao: ChatDao
    private lateinit var offlineRepository: OfflineRepository
    private lateinit var db: AppDatabase

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).build()
        chatDao = db.chatDao()

        offlineRepository = OfflineRepositoryImpl(chatDao)
    }

    @After
    @Throws(IOException::class)
    fun clear() {
        db.close()
    }

    @Test
    fun insertOneMessage_containsOneMessage() = runTest {
        val message = MessageEntityBuilder.aMessage()
            .build()
        offlineRepository.addToHistory(message)
        val messageHistory = offlineRepository.getAllHistory().first()
        assertEquals(messageHistory.size, 1)

        val messageFromDatabase = messageHistory.first()
        assertEquals(messageFromDatabase, message)
    }

    @Test
    fun insertFiveMessages_containsFiveMessages() = runTest {
        val messages = MessageEntityBuilder.messageList(5)
        offlineRepository.addToHistory(messages)

        val messagesFromDatabase = getDatabaseMessagesInOrder()

        assertEquals(messagesFromDatabase, messages)
    }

    @Test
    fun insertFiveMessages_getTheFirstOneById() = runTest {
        val messages = MessageEntityBuilder.messageList(5)
        offlineRepository.addToHistory(messages)

        val firstMessageFromDatabase = offlineRepository.getMessageByMessageId(1)

        assertEquals(messages.first(), firstMessageFromDatabase)
    }

    @Test
    fun insertPendingMessageAndSetDelivered() = runTest {
        val messages = MessageEntityBuilder.messageList(5)
        val pendingMessage = MessageEntityBuilder.aMessage()
            .withDeliveryStatus(DeliveryStatus.PENDING)
            .build()
        offlineRepository.addToHistory(messages + pendingMessage)

        val updatedPendingMessage = pendingMessage.copy(
            date = 12000,
            deliveryStatus = DeliveryStatus.DELIVERED
        )
        offlineRepository.setDelivered(updatedPendingMessage)
        val messageFromDatabaseAfterUpdating =
            offlineRepository.getMessageByMessageId(updatedPendingMessage.messageId!!)

        assertEquals(updatedPendingMessage, messageFromDatabaseAfterUpdating)
    }

    @Test
    fun insertFivePendingOneDeliveredAndDeletePending_oneRemains() = runTest {
        val fivePendingMessages = MessageEntityBuilder.messageList(5) {
            withDeliveryStatus(DeliveryStatus.PENDING)
        }
        val oneDeliveredMessage = MessageEntityBuilder.aMessage()
            .withDeliveryStatus(DeliveryStatus.DELIVERED)
            .build()
        offlineRepository.addToHistory(fivePendingMessages + oneDeliveredMessage)

        offlineRepository.deletePendingMessages()

        val messagesFromDatabase = getDatabaseMessagesInOrder()

        assertEquals(messagesFromDatabase.size, 1)
        assertEquals(messagesFromDatabase.first(), oneDeliveredMessage)
    }

    private suspend fun getDatabaseMessagesInOrder() =
        offlineRepository.getAllHistory().first().reversed()
}

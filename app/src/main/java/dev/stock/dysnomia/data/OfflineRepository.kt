package dev.stock.dysnomia.data

import dev.stock.dysnomia.model.MessageEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

interface OfflineRepository {
    suspend fun addToHistory(messageEntity: MessageEntity)
    suspend fun addToHistory(messageEntityList: List<MessageEntity>)
    fun getAllHistory(): Flow<List<MessageEntity>>
    suspend fun getMessageByMessageId(messageId: Int): MessageEntity?
    suspend fun setDelivered(messageEntity: MessageEntity)
    suspend fun deletePendingMessages()
}

@Singleton
class OfflineRepositoryImpl @Inject constructor(
    private val chatDao: ChatDao
) : OfflineRepository {
    override suspend fun addToHistory(messageEntity: MessageEntity) =
        chatDao.addToHistory(messageEntity)

    override suspend fun addToHistory(messageEntityList: List<MessageEntity>) =
        chatDao.addToHistory(messageEntityList)

    override fun getAllHistory(): Flow<List<MessageEntity>> =
        chatDao.getAllHistory()

    override suspend fun getMessageByMessageId(messageId: Int): MessageEntity? =
        chatDao.getMessageByMessageId(messageId)

    override suspend fun setDelivered(messageEntity: MessageEntity) =
        chatDao.updateMessage(
            messageEntity.copy(
                entityId = chatDao.getEntityIdOfPendingMessage(
                    name = messageEntity.name,
                    message = messageEntity.message
                )
            )
        )

    override suspend fun deletePendingMessages() =
        chatDao.deletePendingMessages()
}

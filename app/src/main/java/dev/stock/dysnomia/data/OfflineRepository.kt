package dev.stock.dysnomia.data

import dev.stock.dysnomia.model.MessageEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

interface DataRepository {
    suspend fun addToHistory(messageEntity: MessageEntity)
    fun getAllHistory(): Flow<List<MessageEntity>>
    suspend fun setDelivered(messageEntity: MessageEntity)
}

@Singleton
class OfflineRepository @Inject constructor(
    private val chatDao: ChatDao
) : DataRepository {
    override suspend fun addToHistory(messageEntity: MessageEntity) =
        chatDao.addToHistory(messageEntity)

    override fun getAllHistory(): Flow<List<MessageEntity>> =
        chatDao.getAllHistory()

    override suspend fun setDelivered(messageEntity: MessageEntity) =
        chatDao.updateMessage(
            messageEntity.copy(
                entityId = chatDao.getEntityIdOfPendingMessage(
                    name = messageEntity.name,
                    message = messageEntity.message
                )
            )
        )
}

package dev.stock.dysnomia.data

import androidx.room.Query
import dev.stock.dysnomia.model.MessageEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

interface DataRepository {
    suspend fun addToHistory(messageEntity: MessageEntity)

    @Query("SELECT * from chat_history")
    fun getAllHistory(): Flow<List<MessageEntity>>
}

@Singleton
class OfflineRepository @Inject constructor(
    private val chatDao: ChatDao
) : DataRepository {
    override suspend fun addToHistory(messageEntity: MessageEntity) =
        chatDao.addToHistory(messageEntity)

    override fun getAllHistory(): Flow<List<MessageEntity>> =
        chatDao.getAllHistory()
}

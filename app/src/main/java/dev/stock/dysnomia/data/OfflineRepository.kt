package dev.stock.dysnomia.data

import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

interface DataRepository {
    suspend fun addToHistory(chatHistoryEntity: ChatHistoryEntity)

    @Query("SELECT * from chat_history")
    fun getAllHistory(): Flow<List<ChatHistoryEntity>>
}

@Singleton
class OfflineRepository @Inject constructor(
    private val chatDao: ChatDao
) : DataRepository {
    override suspend fun addToHistory(chatHistoryEntity: ChatHistoryEntity) =
        chatDao.addToHistory(chatHistoryEntity)

    override fun getAllHistory(): Flow<List<ChatHistoryEntity>> =
        chatDao.getAllHistory()
}

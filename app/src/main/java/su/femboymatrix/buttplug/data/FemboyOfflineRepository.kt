package su.femboymatrix.buttplug.data

import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

interface FemboyDataRepository {
    suspend fun addToHistory(chatHistoryEntity: ChatHistoryEntity)

    @Query("SELECT * from chat_history")
    fun getAllHistory(): Flow<List<ChatHistoryEntity>>
}

@Singleton
class FemboyOfflineRepository @Inject constructor(
    private val chatDao: ChatDao
) : FemboyDataRepository {
    override suspend fun addToHistory(chatHistoryEntity: ChatHistoryEntity) =
        chatDao.addToHistory(chatHistoryEntity)

    override fun getAllHistory(): Flow<List<ChatHistoryEntity>> =
        chatDao.getAllHistory()
}

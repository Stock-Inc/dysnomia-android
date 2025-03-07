package su.femboymatrix.buttplug.data

import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

interface FemboyDataRepository {
    suspend fun addToHistory(consoleHistoryEntity: ConsoleHistoryEntity)

    @Query("SELECT * from console_history")
    fun getAllHistory(): Flow<List<ConsoleHistoryEntity>>
}

@Singleton
class FemboyOfflineRepository @Inject constructor(
    private val consoleDao: ConsoleDao
) : FemboyDataRepository {
    override suspend fun addToHistory(consoleHistoryEntity: ConsoleHistoryEntity) =
        consoleDao.addToHistory(consoleHistoryEntity)

    override fun getAllHistory(): Flow<List<ConsoleHistoryEntity>> =
        consoleDao.getAllHistory()
}

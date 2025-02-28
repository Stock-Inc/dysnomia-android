package su.femboymatrix.buttplug.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ConsoleDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addToHistory(consoleHistoryEntity: ConsoleHistoryEntity)

    @Query("SELECT * from console_history")
    fun getAllHistory(): Flow<List<ConsoleHistoryEntity>>
}
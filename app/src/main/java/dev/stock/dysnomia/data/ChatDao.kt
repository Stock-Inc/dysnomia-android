package dev.stock.dysnomia.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addToHistory(chatHistoryEntity: ChatHistoryEntity)

    @Query("SELECT * from chat_history")
    fun getAllHistory(): Flow<List<ChatHistoryEntity>>
}

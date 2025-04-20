package dev.stock.dysnomia.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.stock.dysnomia.model.MessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addToHistory(messageEntity: MessageEntity)

    @Query("SELECT * from chat_history")
    fun getAllHistory(): Flow<List<MessageEntity>>
}

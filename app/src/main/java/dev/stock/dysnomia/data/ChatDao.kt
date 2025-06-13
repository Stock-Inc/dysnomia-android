package dev.stock.dysnomia.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import dev.stock.dysnomia.model.MessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Upsert
    suspend fun addToHistory(messageEntity: MessageEntity)

    @Query("SELECT * from chat_history ORDER BY date ASC")
    fun getAllHistory(): Flow<List<MessageEntity>>

    @Query(
        "SELECT entity_id FROM chat_history WHERE name = :name AND message = :message AND delivery_status = 'PENDING' LIMIT 1"
    )
    suspend fun getEntityIdOfPendingMessage(name: String, message: String): Int

    @Update
    suspend fun updateMessage(messageEntity: MessageEntity)
}

package dev.stock.dysnomia.data

import androidx.room.Dao
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import dev.stock.dysnomia.model.MessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Upsert
    suspend fun addToHistory(messageEntity: MessageEntity)

    @Upsert
    suspend fun addToHistory(messageEntityList: List<MessageEntity>)

    @Query("SELECT * from chat_history ORDER BY date DESC")
    fun getAllHistory(): Flow<List<MessageEntity>>

    @Query(
        "SELECT entity_id FROM chat_history WHERE name = :name AND message = :message AND delivery_status = 'PENDING' LIMIT 1"
    )
    suspend fun getEntityIdOfPendingMessage(name: String, message: String): Int

    @Query("SELECT * FROM chat_history WHERE message_id = :messageId LIMIT 1")
    suspend fun getMessageByMessageId(messageId: Int): MessageEntity?

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateMessage(messageEntity: MessageEntity)

    @Query("DELETE FROM chat_history WHERE delivery_status = 'PENDING'")
    suspend fun deletePendingMessages()
}

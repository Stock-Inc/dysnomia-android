package dev.stock.dysnomia.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import dev.stock.dysnomia.model.MessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Upsert
    suspend fun addToHistory(messageEntity: MessageEntity)

    @Query("SELECT * from chat_history")
    fun getAllHistory(): Flow<List<MessageEntity>>
}

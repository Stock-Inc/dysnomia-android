package dev.stock.dysnomia.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.lang.System.currentTimeMillis

@Entity(
    tableName = "chat_history",
    indices = [Index(value = ["message_id"], unique = true)]
)
@Serializable
data class ChatHistoryEntity(
    @Transient
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "entity_id")
    val entityId: Int = 0,
    @SerializedName("id")
    @ColumnInfo(name = "message_id")
    val messageId: Int? = null,
    val name: String = "",
    val message: String,
    val date: Long = currentTimeMillis() / 1000,
    @Transient
    @ColumnInfo(name = "is_command")
    val isCommand: Boolean = false
)

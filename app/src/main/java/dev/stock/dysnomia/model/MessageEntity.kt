package dev.stock.dysnomia.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import java.lang.System.currentTimeMillis

@Entity(
    tableName = "chat_history",
    indices = [Index(value = ["message_id"], unique = true)]
)
@Serializable
data class MessageEntity(
    @Transient
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "entity_id")
    val entityId: Int = 0,
    @SerialName("id")
    @ColumnInfo(name = "message_id")
    val messageId: Int? = null,
    val name: String = "",
    val message: String,
    val date: Long = currentTimeMillis() / 1000,
    @Transient
    @ColumnInfo(name = "is_command")
    val isCommand: Boolean = false,
    @Transient
    @ColumnInfo(
        name = "delivery_status",
        defaultValue = "DELIVERED"
    )
    val deliveryStatus: DeliveryStatus = DeliveryStatus.DELIVERED
)

enum class DeliveryStatus {
    PENDING,
    DELIVERED,
    FAILED
}

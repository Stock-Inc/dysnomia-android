package su.femboymatrix.buttplug.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "console_history")
data class ConsoleHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val command: String,
    val result: String
)

package su.femboymatrix.buttplug.data

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [ChatHistoryEntity::class],
    version = 5,
    autoMigrations = [AutoMigration(from = 4, to = 5)]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao
}

package su.femboymatrix.buttplug.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ChatHistoryEntity::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao
}

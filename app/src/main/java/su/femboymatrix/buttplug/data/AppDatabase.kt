package su.femboymatrix.buttplug.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ConsoleHistoryEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun consoleDao(): ConsoleDao
}

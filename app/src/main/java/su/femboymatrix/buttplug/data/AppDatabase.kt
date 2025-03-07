package su.femboymatrix.buttplug.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ConsoleHistoryEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun consoleDao(): ConsoleDao
}

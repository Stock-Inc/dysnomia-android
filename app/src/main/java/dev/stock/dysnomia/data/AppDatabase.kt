package dev.stock.dysnomia.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [ChatHistoryEntity::class],
    version = 4
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao
}

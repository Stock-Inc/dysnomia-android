package dev.stock.dysnomia.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [MessageEntity::class],
    version = 5
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao
}

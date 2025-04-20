package dev.stock.dysnomia.data

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.stock.dysnomia.model.MessageEntity

@Database(
    entities = [MessageEntity::class],
    version = 5
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao
}

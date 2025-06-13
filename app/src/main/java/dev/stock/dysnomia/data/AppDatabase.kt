package dev.stock.dysnomia.data

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import dev.stock.dysnomia.model.MessageEntity

@Database(
    entities = [MessageEntity::class],
    version = 6,
    autoMigrations = [AutoMigration(from = 5, to = 6)]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao
}

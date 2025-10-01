package dev.stock.dysnomia.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.stock.dysnomia.data.AppDatabase
import dev.stock.dysnomia.data.ChatDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun providesDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "app_database")
            .fallbackToDestructiveMigration(true)
            .build()

    @Provides
    @Singleton
    fun providesChatDao(appDatabase: AppDatabase): ChatDao =
        appDatabase.chatDao()
}

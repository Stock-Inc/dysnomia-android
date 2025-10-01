package dev.stock.dysnomia.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.stock.dysnomia.data.NetworkRepository
import dev.stock.dysnomia.data.NetworkRepositoryImpl
import dev.stock.dysnomia.data.OfflineRepository
import dev.stock.dysnomia.data.OfflineRepositoryImpl
import dev.stock.dysnomia.data.PreferencesRepository
import dev.stock.dysnomia.data.PreferencesRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindsNetworkRepository(networkRepositoryImpl: NetworkRepositoryImpl): NetworkRepository

    @Binds
    @Singleton
    abstract fun bindsOfflineRepository(offlineRepositoryImpl: OfflineRepositoryImpl): OfflineRepository

    @Binds
    @Singleton
    abstract fun bindsPreferencesRepository(preferencesRepositoryImpl: PreferencesRepositoryImpl): PreferencesRepository
}

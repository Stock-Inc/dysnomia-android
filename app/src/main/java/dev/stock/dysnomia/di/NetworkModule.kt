package dev.stock.dysnomia.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.stock.dysnomia.data.NetworkRepository
import dev.stock.dysnomia.network.DysnomiaApiService
import dev.stock.dysnomia.utils.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Singleton

@Suppress("unused")
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun providesRetrofitService(): DysnomiaApiService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
            .create(DysnomiaApiService::class.java)
    }

    @Provides
    @Singleton
    fun providesRepository(): NetworkRepository {
        return NetworkRepository(providesRetrofitService())
    }
}

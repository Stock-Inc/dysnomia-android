package dev.stock.dysnomia.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.stock.dysnomia.data.NetworkRepository
import dev.stock.dysnomia.network.DysnomiaApiService
import dev.stock.dysnomia.utils.API_BASE_URL
import dev.stock.dysnomia.utils.WEBSOCKETS_BASE_URL
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun providesRetrofitService(): DysnomiaApiService {
        val json = Json { ignoreUnknownKeys = true }
        return Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(json.asConverterFactory(MediaType.get("application/json")))
            .build()
            .create(DysnomiaApiService::class.java)
    }

    @Provides
    @Singleton
    fun providesStompClient(): StompClient {
        return Stomp.over(
            Stomp.ConnectionProvider.OKHTTP,
            WEBSOCKETS_BASE_URL
        )
    }

    @Provides
    @Singleton
    fun providesRepository(): NetworkRepository {
        return NetworkRepository(providesRetrofitService(), providesStompClient())
    }
}

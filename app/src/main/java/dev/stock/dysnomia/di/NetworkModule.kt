package dev.stock.dysnomia.di

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.stock.dysnomia.data.PreferencesRepository
import dev.stock.dysnomia.network.AuthInterceptor
import dev.stock.dysnomia.network.DysnomiaApiService
import dev.stock.dysnomia.utils.API_BASE_URL
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.websocket.okhttp.OkHttpWebSocketClient
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun providesRetrofit(
        json: Json,
        okHttpClient: OkHttpClient
    ): Retrofit =
        Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(API_BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

    @Provides
    @Singleton
    fun providesRetrofitService(retrofit: Retrofit): DysnomiaApiService =
        retrofit.create(DysnomiaApiService::class.java)

    @Provides
    @Singleton
    fun providesStompClient(okHttpClient: OkHttpClient): StompClient =
        StompClient(OkHttpWebSocketClient(okHttpClient)) {
            autoReceipt = false
        }

    @Provides
    @Singleton
    fun providesJson(): Json =
        Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
            coerceInputValues = true
        }

    @Provides
    @Singleton
    fun providesOkHttpClient(
        preferencesRepository: PreferencesRepository,
        @ApplicationContext context: Context
    ): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(preferencesRepository))
            .addInterceptor(ChuckerInterceptor(context))
            .build()
}

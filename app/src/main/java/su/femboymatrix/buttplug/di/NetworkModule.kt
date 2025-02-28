package su.femboymatrix.buttplug.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import su.femboymatrix.buttplug.data.FemboyNetworkRepository
import su.femboymatrix.buttplug.network.FemboyApiService
import su.femboymatrix.buttplug.utils.BASE_URL
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun providesRetrofitService(): FemboyApiService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
            .create(FemboyApiService::class.java)
    }

    @Provides
    @Singleton
    fun providesRepository(): FemboyNetworkRepository {
        return FemboyNetworkRepository(providesRetrofitService())
    }
}
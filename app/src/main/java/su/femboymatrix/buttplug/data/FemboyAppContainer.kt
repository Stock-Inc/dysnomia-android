package su.femboymatrix.buttplug.data

import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import su.femboymatrix.buttplug.network.FemboyApiService

interface FemboyAppContainer {
    val femboyNetworkRepository: FemboyNetworkRepository
}

class DefaultFemboyAppContainer : FemboyAppContainer {
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.2.2:8080/")
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()
    private val retrofitService: FemboyApiService by lazy {
        retrofit.create(FemboyApiService::class.java)
    }

    override val femboyNetworkRepository: FemboyNetworkRepository by lazy {
        FemboyNetworkRepository(retrofitService)
    }
}
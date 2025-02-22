package su.femboymatrix.buttplug.network

import retrofit2.http.GET
import retrofit2.http.Query


interface FemboyApiService {
    @GET("console")
    suspend fun sendCommand(
        @Query("command") command: String
    ): String
}
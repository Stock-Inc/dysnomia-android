package su.femboymatrix.buttplug.network

import retrofit2.http.GET
import retrofit2.http.Query

interface FemboyApiService {
    @GET("console")
    suspend fun sendMessage(
        @Query("command") message: String
    ): String
}

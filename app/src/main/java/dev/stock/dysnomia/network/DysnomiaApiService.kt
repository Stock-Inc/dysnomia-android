package dev.stock.dysnomia.network

import retrofit2.http.GET
import retrofit2.http.Query

interface DysnomiaApiService {
    @GET("console")
    suspend fun sendCommand(
        @Query("command") command: String
    ): String
}

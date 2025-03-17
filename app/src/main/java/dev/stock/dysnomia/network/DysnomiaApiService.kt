package dev.stock.dysnomia.network

import retrofit2.http.GET
import retrofit2.http.Query

interface DysnomiaApiService {
    @GET("console")
    suspend fun sendMessage(
        @Query("command") message: String
    ): String
}

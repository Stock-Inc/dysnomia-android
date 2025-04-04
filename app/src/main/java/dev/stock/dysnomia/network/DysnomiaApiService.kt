package dev.stock.dysnomia.network

import dev.stock.dysnomia.data.ChatHistoryEntity
import dev.stock.dysnomia.data.MessageBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface DysnomiaApiService {
    @GET("console")
    suspend fun sendCommand(
        @Query("command") command: String
    ): String

    @GET("chat")
    suspend fun getMessages(): List<ChatHistoryEntity>

    @POST("chat")
    suspend fun sendMessage(
        @Body messageBody: MessageBody
    ): ChatHistoryEntity
}

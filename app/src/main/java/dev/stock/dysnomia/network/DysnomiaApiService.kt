package dev.stock.dysnomia.network

import dev.stock.dysnomia.model.CommandSuggestion
import dev.stock.dysnomia.model.MessageEntity
import dev.stock.dysnomia.model.SignInBody
import dev.stock.dysnomia.model.SignInResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface DysnomiaApiService {
    @GET("console")
    suspend fun sendCommand(
        @Query("command") command: String
    ): String

    @GET("all_commands")
    suspend fun getCommandSuggestions(): List<CommandSuggestion>

    @POST("login")
    suspend fun signIn(
        @Body signInBody: SignInBody
    ): SignInResponse

    @GET("message/{messageId}")
    suspend fun getMessageByMessageId(
        @Path("messageId") messageId: Int
    ): MessageEntity
}

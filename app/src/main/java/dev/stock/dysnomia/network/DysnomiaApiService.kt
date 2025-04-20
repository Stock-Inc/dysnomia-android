package dev.stock.dysnomia.network

import dev.stock.dysnomia.data.AuthResponse
import dev.stock.dysnomia.data.MessageBody
import dev.stock.dysnomia.data.MessageEntity
import dev.stock.dysnomia.data.SignInBody
import dev.stock.dysnomia.data.SignUpBody
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
    suspend fun getMessages(): List<MessageEntity>

    @POST("chat")
    suspend fun sendMessage(
        @Body messageBody: MessageBody
    ): MessageEntity

    @POST("auth/sign-up")
    suspend fun signUp(
        @Body signUpBody: SignUpBody
    ): AuthResponse

    @POST("auth/sign-in")
    suspend fun signIn(
        @Body signInBody: SignInBody
    ): AuthResponse
}

package dev.stock.dysnomia.network

import dev.stock.dysnomia.model.AuthResponse
import dev.stock.dysnomia.model.SignInBody
import dev.stock.dysnomia.model.SignUpBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface DysnomiaApiService {
    @GET("console")
    suspend fun sendCommand(
        @Query("command") command: String
    ): String

    @POST("auth/sign-up")
    suspend fun signUp(
        @Body signUpBody: SignUpBody
    ): AuthResponse

    @POST("auth/sign-in")
    suspend fun signIn(
        @Body signInBody: SignInBody
    ): AuthResponse
}

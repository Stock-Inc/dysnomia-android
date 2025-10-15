package dev.stock.dysnomia.network

import dev.stock.dysnomia.model.AuthTokens
import dev.stock.dysnomia.model.ChangeProfileBody
import dev.stock.dysnomia.model.CommandSuggestion
import dev.stock.dysnomia.model.MessageEntity
import dev.stock.dysnomia.model.Profile
import dev.stock.dysnomia.model.SignInBody
import dev.stock.dysnomia.model.SignUpBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
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
    ): AuthTokens

    @POST("signup")
    suspend fun signUp(
        @Body signUpBody: SignUpBody
    ): AuthTokens

    @GET("message/{messageId}")
    suspend fun getMessageByMessageId(
        @Path("messageId") messageId: Int
    ): MessageEntity

    @GET("user/{username}")
    suspend fun getProfile(
        @Path("username") username: String
    ): Profile

    @PATCH("profile/edit_info")
    suspend fun changeProfile(
        @Body changeProfileBody: ChangeProfileBody
    )

    @POST("refresh_token")
    suspend fun refreshToken(
        @Header("Authorization") bearerRefreshToken: String,
    ): AuthTokens
}

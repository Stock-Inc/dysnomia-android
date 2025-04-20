package dev.stock.dysnomia.data

import dev.stock.dysnomia.network.DysnomiaApiService
import javax.inject.Inject
import javax.inject.Singleton

interface Repository {
    suspend fun sendCommand(command: String): String
    suspend fun getMessages(): List<MessageEntity>
    suspend fun sendMessage(messageBody: MessageBody): MessageEntity
    suspend fun signIn(signInBody: SignInBody): AuthResponse
    suspend fun signUp(signUpBody: SignUpBody): AuthResponse
}

@Singleton
class NetworkRepository @Inject constructor(
    private val dysnomiaApiService: DysnomiaApiService
) : Repository {
    override suspend fun sendCommand(command: String): String =
        dysnomiaApiService.sendCommand(command)

    override suspend fun getMessages(): List<MessageEntity> =
        dysnomiaApiService.getMessages()

    override suspend fun sendMessage(messageBody: MessageBody): MessageEntity =
        dysnomiaApiService.sendMessage(messageBody)

    override suspend fun signIn(signInBody: SignInBody): AuthResponse =
        dysnomiaApiService.signIn(signInBody)

    override suspend fun signUp(signUpBody: SignUpBody): AuthResponse =
        dysnomiaApiService.signUp(signUpBody)
}

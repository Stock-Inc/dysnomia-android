package dev.stock.dysnomia.data

import dev.stock.dysnomia.network.DysnomiaApiService
import javax.inject.Inject
import javax.inject.Singleton

interface Repository {
    suspend fun sendCommand(command: String): String
    suspend fun getMessages(): List<ChatHistoryEntity>
    suspend fun sendMessage(messageBody: MessageBody): ChatHistoryEntity
}

@Singleton
class NetworkRepository @Inject constructor(
    private val dysnomiaApiService: DysnomiaApiService
) : Repository {
    override suspend fun sendCommand(command: String): String =
        dysnomiaApiService.sendCommand(command)

    override suspend fun getMessages(): List<ChatHistoryEntity> =
        dysnomiaApiService.getMessages()

    override suspend fun sendMessage(messageBody: MessageBody): ChatHistoryEntity =
        dysnomiaApiService.sendMessage(messageBody)
}

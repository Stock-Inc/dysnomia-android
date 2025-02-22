package su.femboymatrix.buttplug.data

import su.femboymatrix.buttplug.network.FemboyApiService

interface FemboyRepository {
    suspend fun sendCommand(command: String): String
}

class FemboyNetworkRepository(
    private val femboyApiService: FemboyApiService
) : FemboyRepository {
    override suspend fun sendCommand(command: String): String =
        femboyApiService.sendCommand(command)
}
package su.femboymatrix.buttplug.data

import su.femboymatrix.buttplug.network.FemboyApiService
import javax.inject.Inject
import javax.inject.Singleton

interface FemboyRepository {
    suspend fun sendMessage(message: String): String
}

@Singleton
class FemboyNetworkRepository @Inject constructor(
    private val femboyApiService: FemboyApiService
) : FemboyRepository {
    override suspend fun sendMessage(message: String): String =
        femboyApiService.sendMessage(message)
}

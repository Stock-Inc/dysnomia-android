package dev.stock.dysnomia.data

import dev.stock.dysnomia.network.DysnomiaApiService
import javax.inject.Inject
import javax.inject.Singleton

interface Repository {
    suspend fun sendMessage(message: String): String
}

@Singleton
class NetworkRepository @Inject constructor(
    private val dysnomiaApiService: DysnomiaApiService
) : Repository {
    override suspend fun sendMessage(message: String): String =
        dysnomiaApiService.sendMessage(message)
}

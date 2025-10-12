package dev.stock.dysnomia.network

import dev.stock.dysnomia.data.PreferencesRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val hasAuthHeader = request.header("Authorization") != null
        val token = runBlocking {
            preferencesRepository.accessToken.first()
        }

        val authenticatedRequest = if (token.isNotEmpty() && !hasAuthHeader) {
            request.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            request
        }
        return chain.proceed(authenticatedRequest)
    }
}

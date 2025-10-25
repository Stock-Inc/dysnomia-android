package dev.stock.dysnomia.network

import okhttp3.Interceptor
import okhttp3.Response

class TestAuthInterceptor(private val token: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val hasAuthHeader = request.header("Authorization") != null

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

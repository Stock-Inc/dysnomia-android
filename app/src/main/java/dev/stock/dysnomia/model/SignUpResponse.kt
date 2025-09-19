package dev.stock.dysnomia.model

import kotlinx.serialization.Serializable

@Serializable
data class SignUpResponse(
    val accessToken: String,
    val refreshToken: String
)

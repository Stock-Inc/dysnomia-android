package dev.stock.dysnomia.model

import kotlinx.serialization.Serializable

@Serializable
data class SignInBody(
    val username: String,
    val password: String
)

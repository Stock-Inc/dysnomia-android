package dev.stock.dysnomia.model

import kotlinx.serialization.Serializable

@Serializable
data class SignUpBody(
    val username: String,
    val email: String,
    val password: String
)

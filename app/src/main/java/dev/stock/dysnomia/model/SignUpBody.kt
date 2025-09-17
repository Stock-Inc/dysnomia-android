package dev.stock.dysnomia.model

import kotlinx.serialization.Serializable

@Serializable
data class SignUpBody(
    val username: String,
    val password: String
)

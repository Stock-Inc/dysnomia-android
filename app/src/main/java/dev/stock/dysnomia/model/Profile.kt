package dev.stock.dysnomia.model

import kotlinx.serialization.Serializable

@Serializable
data class Profile (
    val username: String,
    val displayName: String?,
    val role: String,
    val bio: String?
)

package dev.stock.dysnomia.model

import kotlinx.serialization.Serializable

@Serializable
data class ChangeProfileBody(
    val displayName: String? = null,
    val bio: String? = null
)

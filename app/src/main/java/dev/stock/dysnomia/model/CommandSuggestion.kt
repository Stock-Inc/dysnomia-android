package dev.stock.dysnomia.model

import kotlinx.serialization.Serializable

@Serializable
data class CommandSuggestion(
    val command: String,
    val result: String
)

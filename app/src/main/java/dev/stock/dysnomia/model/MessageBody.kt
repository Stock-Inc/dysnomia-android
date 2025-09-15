package dev.stock.dysnomia.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MessageBody(
    val name: String,
    val message: String,
    @SerialName("reply_id")
    val replyId: Int
)

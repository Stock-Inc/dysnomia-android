package dev.stock.dysnomia.testutils

import dev.stock.dysnomia.model.DeliveryStatus
import dev.stock.dysnomia.model.MessageEntity

class MessageEntityBuilder {
    private var entityId: Int = 1
    private var messageId: Int = 1
    private var name: String = "testUser"
    private var message: String = "test message"
    private var date: Long = 0L
    private var replyId: Int = 0
    private var isCommand: Boolean = false
    private var deliveryStatus = DeliveryStatus.DELIVERED

    fun withEntityId(entityId: Int) = apply { this.entityId = entityId }
    fun withMessageId(messageId: Int) = apply { this.messageId = messageId }
    fun withName(name: String) = apply { this.name = name }
    fun withMessage(message: String) = apply { this.message = message }
    fun withDate(date: Long) = apply { this.date = date }
    fun withReplyId(replyId: Int) = apply { this.replyId = replyId }
    fun withIsCommand(isCommand: Boolean) = apply { this.isCommand = isCommand }
    fun withDeliveryStatus(deliveryStatus: DeliveryStatus) = apply { this.deliveryStatus = deliveryStatus }

    fun build() = MessageEntity(
        entityId = entityId,
        messageId = messageId,
        name = name,
        message = message,
        date = date,
        replyId = replyId,
        isCommand = isCommand,
        deliveryStatus = deliveryStatus
    )

    companion object {
        fun aMessage() = MessageEntityBuilder()
        fun aCommand() = MessageEntityBuilder()
            .withIsCommand(true)

        fun messageList(
            count: Int,
            configure: MessageEntityBuilder.() -> Unit = {}
        ): List<MessageEntity> {
            return (1..count).map { index ->
                MessageEntityBuilder()
                    .withEntityId(index)
                    .withMessageId(index)
                    .withName("user$index")
                    .withMessage("message $index")
                    .withDate((index * 60).toLong())
                    .apply(configure)
                    .build()
            }
        }

        fun twoMessagesWithReply(configure: MessageEntityBuilder.() -> Unit = {}): List<MessageEntity> {
            return messageList(2) {
                if (entityId == 2) withReplyId(1)
                configure()
            }
        }
    }
}

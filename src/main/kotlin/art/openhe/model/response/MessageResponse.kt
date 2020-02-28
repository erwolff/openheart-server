package art.openhe.model.response

import art.openhe.model.Message
import art.openhe.model.MessageCategory


data class MessageResponse (

    val id: String? = null,
    val authorId: String? = null,
    val authorAvatar: String? = null,
    val recipientId: String? = null,
    val recipientAvatar: String? = null,
    val isReply: Boolean? = false,
    val category: MessageCategory? = null,
    val body: String? = null

) : EntityResponse()

fun Message.toMessageResponse() =
    MessageResponse(id,
        authorId,
        authorAvatar,
        recipientId,
        recipientAvatar,
        isReply,
        category,
        body)
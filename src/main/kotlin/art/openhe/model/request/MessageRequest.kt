package art.openhe.model.request

import art.openhe.model.Message
import art.openhe.model.MessageCategory


data class MessageRequest(

    val authorId: String? = null,
    val authorAvatar: String? = null,
    val recipientId: String? = null,
    val recipientAvatar: String? = null,
    val replyId: String? = null,
    val category: MessageCategory? = null,
    val body: String? = null

) {

    fun applyAsSave() =
        Message(
            authorId = authorId!!,
            authorAvatar = authorAvatar!!,
            recipientId = recipientId,
            recipientAvatar = recipientAvatar,
            replyId = replyId,
            category = category,
            body = body!!)
}
package art.openhe.model.request

import art.openhe.model.Message
import art.openhe.model.MessageCategory


data class MessageRequest (

    val authorId: String? = null,
    val isReply: Boolean? = false,
    val category: MessageCategory? = null,
    val body: String? = null

) {

    fun toMessage() =
        Message(authorId = authorId,
            category = category,
            body = body)

}
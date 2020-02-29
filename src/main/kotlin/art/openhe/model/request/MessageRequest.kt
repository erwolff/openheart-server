package art.openhe.model.request

import art.openhe.model.Message
import art.openhe.model.MessageCategory
import org.bson.types.ObjectId


data class MessageRequest (

    val authorId: String? = null,
    val authorAvatar: String? = null,
    val recipientId: String? = null,
    val recipientAvatar: String? = null,
    val isReply: Boolean? = false,
    val category: MessageCategory? = null,
    val body: String? = null

)
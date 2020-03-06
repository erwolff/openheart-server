package art.openhe.model.request

import art.openhe.model.Letter
import art.openhe.model.LetterCategory


data class LetterRequest(

    val authorId: String? = null,
    val authorAvatar: String? = null,
    val recipientId: String? = null,
    val recipientAvatar: String? = null,
    val replyId: String? = null,
    val category: LetterCategory? = null,
    val body: String? = null

) {

    fun applyAsSave() =
        Letter(
            authorId = authorId!!,
            authorAvatar = authorAvatar!!,
            recipientId = recipientId,
            recipientAvatar = recipientAvatar,
            replyId = replyId,
            category = category,
            body = body!!)
}
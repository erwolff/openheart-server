package art.openhe.model.request

import art.openhe.model.Letter
import art.openhe.model.LetterCategory
import org.joda.time.DateTimeUtils


data class LetterRequest(

    val authorId: String? = null,
    val authorAvatar: String? = null,
    val recipientId: String? = null,
    val recipientAvatar: String? = null,
    val replyId: String? = null,
    val category: LetterCategory? = null,
    val body: String? = null,
    val writtenTimestamp: Long? = null

) {

    fun applyAsSave() =
        Letter(
            authorId = authorId!!,
            authorAvatar = authorAvatar!!,
            recipientId = recipientId,
            recipientAvatar = recipientAvatar,
            replyId = replyId,
            category = category,
            body = body!!,
            writtenTimestamp = writtenTimestamp ?: DateTimeUtils.currentTimeMillis())
}
package art.openhe.model.response

import art.openhe.model.Letter
import art.openhe.model.LetterCategory


data class LetterResponse (

    val id: String? = null,
    val authorId: String? = null,
    val authorAvatar: String? = null,
    val recipientId: String? = null,
    val recipientAvatar: String? = null,
    val replyId: String? = null,
    val category: LetterCategory? = null,
    val body: String? = null,
    val writtenTimestamp: Long? = 0,
    val sentTimestamp: Long? = 0,
    val readTimestamp: Long? = 0

) : EntityResponse()

fun Letter.toLetterResponse() =
    toLetterResponse(recipientId, recipientAvatar)

fun Letter.toLetterResponse(recipientId: String?, recipientAvatar: String?) =
    LetterResponse(id,
        authorId,
        authorAvatar,
        recipientId,
        recipientAvatar,
        replyId,
        category,
        body,
        writtenTimestamp,
        sentTimestamp,
        readTimestamp)
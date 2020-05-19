package art.openhe.model.response

import art.openhe.model.Letter
import art.openhe.model.LetterCategory


data class LetterResponse (

    val id: String? = null,
    val authorId: String? = null,
    val authorAvatar: String? = null,
    val recipientId: String? = null,
    val recipientAvatar: String? = null,
    val parentId: String? = null,
    val childId: String? = null,
    val parentPreview: String? = null,
    val childPreview: String? = null,
    val category: LetterCategory? = null,
    val body: String? = null,
    val writtenTimestamp: Long? = 0,
    val sentTimestamp: Long? = 0,
    val readTimestamp: Long? = 0,
    val hearted: Boolean? = null

) : EntityResponse()

fun Letter.toLetterResponse() =
    LetterResponse(id,
        authorId,
        authorAvatar,
        recipientId,
        recipientAvatar,
        parentId,
        childId,
        parentPreview,
        childPreview,
        category,
        body,
        writtenTimestamp,
        sentTimestamp,
        readTimestamp,
        hearted)
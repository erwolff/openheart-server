package art.openhe.model.request

import art.openhe.model.Letter
import art.openhe.model.LetterCategory
import art.openhe.util.UpdateQuery
import org.joda.time.DateTimeUtils


data class LetterRequest(

    val authorId: String? = null,
    val authorAvatar: String? = null,
    val recipientId: String? = null,
    val recipientAvatar: String? = null,
    val parentId: String? = null,
    val childId: String? = null,
    val category: LetterCategory? = null,
    val body: String? = null,
    val writtenTimestamp: Long? = null,
    val readTimestamp: Long? = null

) {

    fun applyAsSave() =
        Letter(
            authorId = authorId!!,
            authorAvatar = authorAvatar!!,
            recipientId = recipientId,
            recipientAvatar = recipientAvatar,
            parentId = parentId,
            childId = childId,
            category = category,
            body = body!!,
            writtenTimestamp = writtenTimestamp ?: DateTimeUtils.currentTimeMillis(),
            readTimestamp = readTimestamp ?:  0)

    // currently only updating the readTimestamp is allowed
    fun toUpdateQuery() =
        UpdateQuery(
            readTimestamp?.let { "readTimestamp" to it }
        )
}
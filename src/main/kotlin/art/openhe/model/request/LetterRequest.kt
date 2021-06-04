package art.openhe.model.request

import art.openhe.model.Letter
import art.openhe.model.LetterCategory
import art.openhe.util.DbUpdate
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL
import org.joda.time.DateTimeUtils


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(NON_NULL)
data class LetterRequest(

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

    fun toLetter(authorId: String) =
        Letter(
            authorId = authorId,
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
    fun toUpdateQuery(letterId: String) =
        DbUpdate(
            letterId,
            readTimestamp?.let { "readTimestamp" to it }
        )
}
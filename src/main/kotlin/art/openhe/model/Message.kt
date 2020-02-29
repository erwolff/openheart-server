package art.openhe.model

import com.fasterxml.jackson.annotation.JsonInclude
import org.jongo.marshall.jackson.oid.MongoId
import org.jongo.marshall.jackson.oid.MongoObjectId

@JsonInclude(JsonInclude.Include.ALWAYS)
data class Message(

    @MongoId @MongoObjectId val id: String,
    val authorId: String,
    val authorAvatar: String,
    val recipientId: String? = null,
    val recipientAvatar: String? = null,
    val isReply: Boolean,
    val category: MessageCategory? = null,
    val body: String,
    val sentTimestamp: Long = 0,
    val readTimestamp: Long = 0

) {

    fun update(
        recipientId: String? = null,
        recipientAvatar: String? = null,
        sentTimestamp: Long? = null,
        readTimestamp: Long? = null
    ): Message =
        Message(
            id,
            authorId,
            authorAvatar,
            recipientId ?: this.recipientId,
            recipientAvatar ?: this.recipientAvatar,
            isReply,
            category,
            body,
            sentTimestamp ?: this.sentTimestamp,
            readTimestamp ?: this.readTimestamp
        )
}
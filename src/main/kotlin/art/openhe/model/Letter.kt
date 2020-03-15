package art.openhe.model

import com.fasterxml.jackson.annotation.JsonInclude
import org.bson.types.ObjectId
import org.jongo.marshall.jackson.oid.MongoId
import org.jongo.marshall.jackson.oid.MongoObjectId

@JsonInclude(JsonInclude.Include.ALWAYS)
data class Letter (

    @MongoId @MongoObjectId override val id: String = ObjectId().toHexString(),
    val authorId: String,
    val authorAvatar: String,
    val recipientId: String? = null,
    val recipientAvatar: String? = null,
    val replyId: String? = null,
    val category: LetterCategory? = null,
    val body: String,
    val writtenTimestamp: Long = 0,
    val sentTimestamp: Long = 0,
    val readTimestamp: Long = 0,
    val flagged: Boolean? = false

) : DbObject() {

    fun update(
        id: String? = null,
        authorId: String? = null,
        authorAvatar: String? = null,
        recipientId: String? = null,
        recipientAvatar: String? = null,
        replyId: String? = null,
        category: LetterCategory? = null,
        body: String? = null,
        writtenTimestamp: Long? = null,
        sentTimestamp: Long? = null,
        readTimestamp: Long? = null,
        flagged: Boolean? = null
    ): Letter =
        Letter(
            id ?: this.id,
            authorId ?: this.authorId,
            authorAvatar ?: this.authorAvatar,
            recipientId ?: this.recipientId,
            recipientAvatar ?: this.recipientAvatar,
            replyId ?: this.replyId,
            category ?: this.category,
            body ?: this.body,
            writtenTimestamp ?: this.writtenTimestamp,
            sentTimestamp ?: this.sentTimestamp,
            readTimestamp ?: this.readTimestamp,
            flagged ?: this.flagged
        )

}


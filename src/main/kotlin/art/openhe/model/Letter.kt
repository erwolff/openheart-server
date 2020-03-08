package art.openhe.model

import com.fasterxml.jackson.annotation.JsonInclude
import org.bson.types.ObjectId
import org.jongo.marshall.jackson.oid.MongoId
import org.jongo.marshall.jackson.oid.MongoObjectId

@JsonInclude(JsonInclude.Include.ALWAYS)
data class Letter(

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
    val readTimestamp: Long = 0

) : DbObject()
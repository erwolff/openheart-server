package art.openhe.model

import com.fasterxml.jackson.annotation.JsonInclude
import org.bson.types.ObjectId
import org.jongo.marshall.jackson.oid.MongoId
import org.jongo.marshall.jackson.oid.MongoObjectId

@JsonInclude(JsonInclude.Include.ALWAYS)
data class User (

    @MongoId @MongoObjectId override val id: String = ObjectId().toHexString(),
    val email: String,
    val googleId: String,
    val avatar: String,
    val lastSentLetterTimestamp: Long = 0,
    val lastReceivedLetterTimestamp: Long = 0,
    val hearts: Long = 0

) : DbObject()

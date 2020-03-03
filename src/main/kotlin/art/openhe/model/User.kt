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
    val lastSentMessageTimestamp: Long = 0,
    val lastReceivedMessageTimestamp: Long = 0,
    val numHearts: Long = 0

) : DbObject()

package art.openhe.model

import com.fasterxml.jackson.annotation.JsonInclude
import org.jongo.marshall.jackson.oid.MongoId
import org.jongo.marshall.jackson.oid.MongoObjectId

@JsonInclude(JsonInclude.Include.ALWAYS)
data class User (

    @MongoId @MongoObjectId val id: String,
    val email: String,
    val password: String,
    val avatar: String,
    val lastSentMessageTimestamp: Long = 0,
    val lastReceivedMessageTimestamp: Long = 0,
    val numHearts: Long = 0

)

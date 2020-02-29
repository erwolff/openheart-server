package art.openhe.model

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.ALWAYS)
data class Message (

    val id: String,
    val authorId: String,
    val authorAvatar: String,
    val recipientId: String,
    val recipientAvatar: String,
    val isReply: Boolean,
    val category: MessageCategory,
    val body: String

)
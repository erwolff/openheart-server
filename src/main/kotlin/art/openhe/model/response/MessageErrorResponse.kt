package art.openhe.model.response

import javax.ws.rs.core.Response


data class MessageErrorResponse (

    override val status: Response.Status,
    val id: String? = null,
    val authorId: String? = null,
    val authorAvatar: String? = null,
    val recipientId: String? = null,
    val recipientAvatar: String? = null,
    val isReply: String? = null,
    val category: String? = null,
    val body: String? = null

) : ErrorResponse()
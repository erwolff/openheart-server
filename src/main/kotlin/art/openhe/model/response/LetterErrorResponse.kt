package art.openhe.model.response

import javax.ws.rs.core.Response


data class LetterErrorResponse (

    override val status: Response.Status,
    override val message: String? = null,
    val id: String? = null,
    val authorId: String? = null,
    val authorAvatar: String? = null,
    val recipientId: String? = null,
    val recipientAvatar: String? = null,
    val reply: String? = null,
    val category: String? = null,
    val body: String? = null

) : ErrorResponse()
package art.openhe.model.response

import javax.ws.rs.core.Response


data class UserErrorResponse (

    override val status: Response.Status,
    override val message: String? = null,
    val id: String? = null,
    val email: String? = null,
    val avatar: String? = null

) : ErrorResponse()
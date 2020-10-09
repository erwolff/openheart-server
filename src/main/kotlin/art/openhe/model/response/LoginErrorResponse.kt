package art.openhe.model.response

import javax.ws.rs.core.Response


data class LoginErrorResponse (

    override val status: Response.Status,
    override val message: String? = null,
    val googleIdToken: String? = null

) : ErrorResponse
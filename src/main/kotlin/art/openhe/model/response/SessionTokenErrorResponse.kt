package art.openhe.model.response

import javax.ws.rs.core.Response


data class SessionTokenErrorResponse(

    override val status: Response.Status,
    override val message: String? = null

) : ErrorResponse
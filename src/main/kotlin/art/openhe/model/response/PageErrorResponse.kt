package art.openhe.model.response

import javax.ws.rs.core.Response


class PageErrorResponse (

    override val status: Response.Status,
    override val message: String? = null,
    val page: String? = null,
    val size: String? = null

) : ErrorResponse()
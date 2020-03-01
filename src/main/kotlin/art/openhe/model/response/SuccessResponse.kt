package art.openhe.model.response

import javax.ws.rs.core.Response


abstract class SuccessResponse {

    @Suppress("UNCHECKED_CAST")
    fun <SUCCESS : SuccessResponse, ERROR : ErrorResponse> toApiResponse(): ApiResponse<SUCCESS, ERROR> =
        ApiResponse(this as SUCCESS)

    open fun toResponse(): Response = Response.ok().build()
}
package art.openhe.model.response

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import javax.ws.rs.core.Response

@JsonInclude(JsonInclude.Include.NON_NULL)
abstract class ErrorResponse {

    @get:JsonIgnore
    abstract val status: Response.Status

    @Suppress("UNCHECKED_CAST")
    fun <ENTITY: EntityResponse, ERROR: ErrorResponse> toApiResponse(): ApiResponse<ENTITY, ERROR> =
        ApiResponse(this as ERROR)

    fun toResponse(): Response = Response.status(status).entity(ErrorResponseWrapper(this)).build()

    class ErrorResponseWrapper(val errors: ErrorResponse)
}
package art.openhe.model.response

import com.fasterxml.jackson.annotation.JsonInclude
import javax.ws.rs.core.Response

@JsonInclude(JsonInclude.Include.NON_NULL)
abstract class EntityResponse {

    @Suppress("UNCHECKED_CAST")
    fun <ENTITY : EntityResponse, ERROR : ErrorResponse> toApiResponse(): ApiResponse<ENTITY, ERROR> =
        ApiResponse(this as ENTITY)

    fun toResponse(): Response = Response.ok().entity(EntityResponseWrapper(this)).build()

    class EntityResponseWrapper(val content: EntityResponse)
}
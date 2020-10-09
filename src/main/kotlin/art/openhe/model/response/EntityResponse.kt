package art.openhe.model.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL
import javax.ws.rs.core.Response

@JsonInclude(NON_NULL)
interface EntityResponse {

    fun toResponse(): Response = Response.ok().entity(EntityResponseWrapper(this)).build()

    class EntityResponseWrapper(val content: EntityResponse)
}
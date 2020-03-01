package art.openhe.model.response

import com.fasterxml.jackson.annotation.JsonInclude
import javax.ws.rs.core.Response

@JsonInclude(JsonInclude.Include.NON_NULL)
abstract class EntityResponse : SuccessResponse() {

    override fun toResponse(): Response = Response.ok().entity(EntityResponseWrapper(this)).build()

    class EntityResponseWrapper(val content: EntityResponse)
}
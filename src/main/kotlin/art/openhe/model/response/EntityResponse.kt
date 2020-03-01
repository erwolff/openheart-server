package art.openhe.model.response

import javax.ws.rs.core.Response


abstract class EntityResponse : HandlerResponse() {

    override fun toResponse(): Response = Response.ok().entity(EntityResponseWrapper(this)).build()

    class EntityResponseWrapper(val content: EntityResponse)
}
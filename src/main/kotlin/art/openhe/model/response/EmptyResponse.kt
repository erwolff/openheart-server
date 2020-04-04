package art.openhe.model.response

import javax.ws.rs.core.Response


class EmptyResponse : HandlerResponse() {

    override fun toResponse(): Response = Response.noContent().build()
}

fun Any.toEmptyResponse(): HandlerResponse = EmptyResponse()
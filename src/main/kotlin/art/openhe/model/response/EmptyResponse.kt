package art.openhe.model.response

import art.openhe.model.DbObject
import javax.ws.rs.core.Response


class EmptyResponse : HandlerResponse() {

    override fun toResponse(): Response = Response.noContent().build()
}

fun DbObject.toEmptyResponse(): HandlerResponse = EmptyResponse()
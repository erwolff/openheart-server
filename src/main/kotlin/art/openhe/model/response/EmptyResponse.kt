package art.openhe.model.response

import art.openhe.model.DbObject
import javax.ws.rs.core.Response


class EmptyResponse : EntityResponse {

    override fun toResponse(): Response = Response.noContent().build()
}

fun DbObject.toEmptyResponse() = EmptyResponse()
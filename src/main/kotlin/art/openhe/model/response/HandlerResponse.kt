package art.openhe.model.response

import com.fasterxml.jackson.annotation.JsonInclude
import javax.ws.rs.core.Response


@JsonInclude(JsonInclude.Include.NON_NULL)
abstract class HandlerResponse {

    open fun toResponse(): Response = Response.ok().build()
}
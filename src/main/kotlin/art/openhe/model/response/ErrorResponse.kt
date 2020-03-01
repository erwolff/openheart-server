package art.openhe.model.response

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.ws.rs.core.Response


abstract class ErrorResponse : HandlerResponse() {

    @get:JsonIgnore
    abstract val status: Response.Status

    override fun toResponse(): Response = Response.status(status).entity(ErrorResponseWrapper(this)).build()

    class ErrorResponseWrapper(val errors: ErrorResponse)
}
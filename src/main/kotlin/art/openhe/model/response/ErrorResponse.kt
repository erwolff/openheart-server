package art.openhe.model.response

import art.openhe.util.Mapper
import art.openhe.util.ext.log
import art.openhe.util.logError
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL
import javax.ws.rs.core.Response

@JsonInclude(NON_NULL)
interface ErrorResponse {

    @get:JsonIgnore val status: Response.Status

    val message: String?

    fun toResponse(): Response {
        val entity = Mapper.toJsonString(ErrorResponseWrapper(this))
        logError { "Received invalid request - replying with status: ${status.log()} and response: $entity" }
        return Response.status(status).entity(entity).build()
    }

    class ErrorResponseWrapper(val errors: ErrorResponse)
}
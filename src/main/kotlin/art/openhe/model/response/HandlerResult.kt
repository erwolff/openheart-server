package art.openhe.model.response

import javax.ws.rs.core.Response


sealed class HandlerResult<out ENTITY : EntityResponse, out ERROR : ErrorResponse> {

    data class Success<out ENTITY : EntityResponse>(val entity: ENTITY) : HandlerResult<ENTITY, Nothing>()
    data class Failure<out ERROR : ErrorResponse>(val error: ERROR) : HandlerResult<Nothing, ERROR>()

    fun toResponse(): Response = when (this) {
        is Success -> this.entity.toResponse()
        is Failure -> this.error.toResponse()
    }
}

fun <ENTITY: EntityResponse> ENTITY.asSuccess() = HandlerResult.Success(this)
fun <ERROR: ErrorResponse> ERROR.asFailure() = HandlerResult.Failure(this)
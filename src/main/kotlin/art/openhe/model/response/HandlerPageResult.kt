package art.openhe.model.response

import javax.ws.rs.core.Response


sealed class HandlerPageResult<out ENTITY : EntityResponse, out ERROR : ErrorResponse> {

    data class Success<out ENTITY : EntityResponse>(val page: PageResponse<out ENTITY>) : HandlerPageResult<ENTITY, Nothing>()
    data class Failure<out ERROR: ErrorResponse>(val error: ERROR) : HandlerPageResult<Nothing, ERROR>()

    fun toResponse(): Response = when (this) {
        is Success -> this.page.toResponse()
        is Failure -> this.error.toResponse()
    }
}

fun <ENTITY: EntityResponse> PageResponse<ENTITY>.asSuccess() = HandlerPageResult.Success(this)
fun <ERROR: ErrorResponse> ERROR.asPageFailure() = HandlerPageResult.Failure(this)
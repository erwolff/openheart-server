package art.openhe.model.response

import javax.ws.rs.core.Response


class ApiResponse<ENTITY : EntityResponse, ERROR : ErrorResponse> {
    val entity: ENTITY?
    val error: ERROR?

    constructor(entity: ENTITY) {
        this.entity = entity
        error = null
    }

    constructor(error: ERROR) {
        this.error = error
        entity = null
    }

    val isSuccess: Boolean
        get() = entity != null

    fun toResponse(): Response {
        return entity?.toResponse() ?: error!!.toResponse()
    }
}
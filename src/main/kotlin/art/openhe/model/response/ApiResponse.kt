package art.openhe.model.response

import javax.ws.rs.core.Response


class ApiResponse<SUCCESS : SuccessResponse, ERROR : ErrorResponse> {
    val success: SUCCESS?
    val error: ERROR?

    constructor(success: SUCCESS) {
        this.success = success
        error = null
    }

    constructor(error: ERROR) {
        this.error = error
        success = null
    }

    val isSuccess: Boolean
        get() = success != null

    fun toResponse(): Response {
        return success?.toResponse() ?: error!!.toResponse()
    }
}
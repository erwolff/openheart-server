package art.openhe.validator

import art.openhe.model.request.LetterRequest
import art.openhe.model.response.*
import javax.inject.Singleton
import javax.ws.rs.core.Response


/**
 * Validates LetterRequests
 */
@Singleton
class LetterRequestValidator : Validator {

    /**
     * Validates the provided LetterRequest ensuring that:
     * - the 'authorAvatar' is non-null
     * - the 'body' is non-null
     */
    fun validateCreate(
        request: LetterRequest
    ) : LetterErrorResponse? =
        with(request) {
            authorAvatar
                ?: return invalidAuthorAvatar

            body
                ?: return invalidBody

            return valid
        }


    companion object {
        private val invalidAuthorAvatar = LetterErrorResponse(Response.Status.BAD_REQUEST, authorAvatar = "An authorAvatar is required")
        private val invalidBody = LetterErrorResponse(Response.Status.BAD_REQUEST, body = "A body is required")
    }

}
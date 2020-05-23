package art.openhe.resource.filter.authorization

import art.openhe.config.EnvConfig
import art.openhe.dao.LetterDao
import art.openhe.model.Letter
import art.openhe.model.response.LetterErrorResponse
import io.novocaine.Novocaine
import javax.annotation.Priority
import javax.ws.rs.Priorities
import javax.ws.rs.WebApplicationException
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.ContainerRequestFilter
import javax.ws.rs.core.Response
import javax.ws.rs.ext.Provider


/**
 * Ensures the userId in the security context is either the recipient
 * or the author of the letter with the id supplied in the path
 */
@AuthorRecipientAuthorization
@Provider
@Priority(Priorities.AUTHORIZATION)
class AuthorRecipientAuthorizationFilter : ContainerRequestFilter {

    override fun filter(requestContext: ContainerRequestContext?) {
        val userId = requestContext?.securityContext?.userPrincipal?.name
            ?: throw WebApplicationException(Response.Status.UNAUTHORIZED)

        val letterIds = requestContext.uriInfo.pathParameters["id"]

        if (letterIds == null || letterIds.size != 1) {
            requestContext.abortWith(LetterErrorResponse(Response.Status.BAD_REQUEST,
                    "Only one letter id may be supplied in the URL path").toResponse())
            return
        }

        if (letterIds[0] == Novocaine.get(EnvConfig::class.java).welcomeLetterId()) {
            // all users have access to the welcome letter
            return
        }

        val letter = Novocaine.get(LetterDao::class.java).findById(letterIds[0])

        if (!isAuthorOrRecipient(userId, letter) || !isViewable(userId, letter!!)) {
            requestContext.abortWith(LetterErrorResponse(Response.Status.NOT_FOUND,
                    "A letter with id ${letterIds[0]} does not exist").toResponse())
        }

        // valid
    }


    private fun isAuthorOrRecipient(userId: String, letter: Letter?) =
        letter != null && (userId == letter.recipientId || userId == letter.authorId)

    /**
     * A letter is viewable if the userId is the author or if the letter hasn't been deleted
     */
    private fun isViewable(userId: String, letter: Letter) =
        userId == letter.authorId || letter.deleted != true
}
package art.openhe.resource

import art.openhe.handler.LetterRequestHandler
import art.openhe.model.request.LetterRequest
import art.openhe.resource.filter.authentication.SessionAuthentication
import art.openhe.resource.filter.authorization.AuthorRecipientAuthorization
import art.openhe.resource.filter.authorization.RecipientAuthorization
import javax.inject.Inject
import javax.inject.Singleton
import javax.ws.rs.*
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.core.SecurityContext

/**
 * REST Resource which handles all Letter-related requests
 */
@Path("/letters")
@SessionAuthentication
@Produces(MediaType.APPLICATION_JSON)
@Singleton
class LetterResource
@Inject constructor(
    private val handler: LetterRequestHandler
): Resource {

    /**
     * Returns a LetterResponse representing the
     * Letter object with the provided id
     */
    @GET
    @Path("/{id}")
    @AuthorRecipientAuthorization
    fun getLetter(
        @PathParam("id") id: String
    ): Response =
        handler.getLetter(id).toResponse()


    /**
     * Creates a new Letter object based on the
     * supplied LetterRequest and returns a
     * LetterResponse representing this newly created
     * Letter
     */
    @POST
    fun writeLetter(
        request: LetterRequest,
        @Context context: SecurityContext
    ): Response =
        handler.writeLetter(
            request = request,
            authorId = context.userPrincipal.name
        ).toResponse()


    /**
     * Returns a paginated entity of LetterResponse objects
     * which the calling entity has authored - filtered by
     * the supplied params
     */
    //TODO: Add sort, refactor into pageable object
    @GET
    @Path("/sent")
    fun getSentLetters(
        @QueryParam("page") page: Int,
        @QueryParam("size") size: Int,
        @QueryParam("hearted") hearted: Boolean?,
        @QueryParam("reply") reply: Boolean?,
        @Context context: SecurityContext
    ): Response =
        handler.getSentLetters(
            authorId = context.userPrincipal.name,
            page = page,
            size = size,
            hearted = hearted,
            reply = reply
        ).toResponse()


    /**
     * Returns a paginated entity of LetterResponse objects
     * which the calling entity has received - filtered by
     * the supplied params
     */
    //TODO: Add sort, refactor into pageable object
    @GET
    @Path("/received")
    fun getReceivedLetters(
        @QueryParam("page") page: Int,
        @QueryParam("size") size: Int,
        @Context context: SecurityContext
    ): Response =
        handler.getReceivedLetters(
            recipientId = context.userPrincipal.name,
            page = page,
            size = size
        ).toResponse()


    /**
     * Sets hearted: true
     * Success: 204 No Content
     */
    @PUT
    @Path("/{id}/heart")
    @RecipientAuthorization
    fun heartLetter(
        @PathParam("id") id: String
    ): Response =
        handler.heartLetter(id).toResponse()


    /**
     * Sets flagged: true and deleted: true
     * Success: 204 No Content
     */
    @PUT
    @Path("/{id}/report")
    @RecipientAuthorization
    fun reportLetter(
        @PathParam("id") id: String
    ): Response =
        handler.reportLetter(id).toResponse()


    /**
     * Sets readTimestamp: now
     * Success: 204 No Content
     */
    @PUT
    @Path("/{id}/read")
    @RecipientAuthorization
    fun markAsRead(
        @PathParam("id") id: String
    ): Response =
        handler.markAsRead(id).toResponse()


    /**
     * Sets deleted: true
     * Success: 204 No Content
     */
    @DELETE
    @Path("/{id}")
    @RecipientAuthorization
    fun deleteLetter(
        @PathParam("id") id: String
    ): Response =
        handler.deleteLetter(id).toResponse()
}
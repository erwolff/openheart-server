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

@Path("/letters")
@SessionAuthentication
@Produces(MediaType.APPLICATION_JSON)
@Singleton
class LetterResource
@Inject constructor(
    private val handler: LetterRequestHandler
): Resource {

    @POST
    fun writeLetter(
        request: LetterRequest,
        @Context context: SecurityContext
    ): Response =
        handler.writeLetter(request, context.userPrincipal.name).toResponse()

    @GET
    @Path("/{id}")
    @AuthorRecipientAuthorization
    fun getLetter(
        @PathParam("id") id: String
    ): Response =
        handler.getLetter(id).toResponse()

    // TODO: Currently we don't allow blanket updates
    /*@PUT
    @Path("/{id}")
    fun updateLetter(@PathParam("id") id: String, request: LetterRequest): Response =
        handler.updateLetter(id, request).toResponse()*/

    @GET
    @Path("/sent")
    fun getSentLetters(
        @QueryParam("page") page: Int,
        @QueryParam("size") size: Int,
        @QueryParam("hearted") hearted: Boolean?,
        @QueryParam("reply") reply: Boolean?,
        @Context context: SecurityContext
    ): Response =
        handler.getSentLetters(context.userPrincipal.name, page, size).toResponse()

    @GET
    @Path("/received")
    fun getReceivedLetters(
        @QueryParam("page") page: Int,
        @QueryParam("size") size: Int,
        @Context context: SecurityContext
    ): Response =
        handler.getReceivedLetters(context.userPrincipal.name, page, size).toResponse()

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
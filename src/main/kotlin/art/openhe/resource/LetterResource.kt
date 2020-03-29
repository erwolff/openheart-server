package art.openhe.resource

import art.openhe.handler.LetterRequestHandler
import art.openhe.model.request.LetterRequest
import art.openhe.resource.filter.SessionAuthentication
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
@Inject constructor(private val handler: LetterRequestHandler): Resource {

    @POST
    fun sendLetter(request: LetterRequest): Response =
        handler.receiveLetter(request).toResponse()

    // TODO: Add a PathLetterId filter which checks the user has access to the letter

    @GET
    @Path("/{id}")
    fun getLetter(@PathParam("id") id: String): Response =
        handler.getLetter(id).toResponse()

    @PUT
    @Path("/{id}")
    fun updateLetter(@PathParam("id") id: String, request: LetterRequest): Response =
        handler.updateLetter(id, request).toResponse()

    @GET
    @Path("/sent")
    fun getSentLetters(@QueryParam("page") page: Int,
                       @QueryParam("size") size: Int,
                       @QueryParam("hearted") hearted: Boolean?,
                       @QueryParam("reply") reply: Boolean?,
                       @Context securityContext: SecurityContext): Response =
        handler.getSentLetters(securityContext.userPrincipal.name, page, size).toResponse()

    @GET
    @Path("/received")
    fun getReceivedLetters(@QueryParam("page") page: Int,
                           @QueryParam("size") size: Int,
                           @Context securityContext: SecurityContext): Response =
        handler.getReceivedLetters(securityContext.userPrincipal.name, page, size).toResponse()

    @PUT
    @Path("/{id}/heart")
    fun heartLetter(@PathParam("id") id: String): Response =
        handler.heartLetter(id).toResponse()

    @PUT
    @Path("/{id}/read")
    fun markAsRead(@PathParam("id") id: String): Response =
        handler.markAsRead(id).toResponse()
}
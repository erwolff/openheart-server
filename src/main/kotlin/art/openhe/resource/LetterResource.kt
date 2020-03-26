package art.openhe.resource

import art.openhe.handler.LetterRequestHandler
import art.openhe.model.request.LetterRequest
import javax.inject.Inject
import javax.inject.Singleton
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("/letters")
@Produces(MediaType.APPLICATION_JSON)
@Singleton
class LetterResource
@Inject constructor(private val handler: LetterRequestHandler): Resource {

    @POST
    fun sendLetter(request: LetterRequest): Response =
        handler.receiveLetter(request).toResponse()

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
    fun getSentLetters(@QueryParam("userId") authorId: String, // TODO: retrieve this from security context
                   @QueryParam("page") page: Int,
                   @QueryParam("size") size: Int): Response =
        handler.getSentLetters(authorId, page, size).toResponse()

    @GET
    @Path("/received")
    fun getReceivedLetters(@QueryParam("userId") recipientId: String, // TODO: retrieve this from security context
                   @QueryParam("page") page: Int,
                   @QueryParam("size") size: Int): Response =
        handler.getReceivedLetters(recipientId, page, size).toResponse()

    @PUT
    @Path("/{id}/heart")
    fun heartLetter(@PathParam("id") id: String): Response =
        handler.heartLetter(id).toResponse()

    @PUT
    @Path("/{id}/read")
    fun markAsRead(@PathParam("id") id: String): Response =
        handler.markAsRead(id).toResponse()
}
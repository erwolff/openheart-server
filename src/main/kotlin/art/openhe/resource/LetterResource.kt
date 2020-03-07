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
    fun receiveLetter(request: LetterRequest): Response =
        handler.receiveLetter(request).toResponse()

    @GET
    @Path("/{id}")
    fun getLetter(@PathParam("id") id: String): Response =
        handler.getLetter(id).toResponse()

    @GET
    fun getLetters(@QueryParam("authorId") authorId: String, // TODO: retrieve this from security context
                   @QueryParam("page") page: Int,
                   @QueryParam("size") size: Int): Response =
        handler.getLetters(authorId, page, size).toResponse();
}
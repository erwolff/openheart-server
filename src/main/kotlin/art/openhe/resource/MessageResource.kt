package art.openhe.resource

import art.openhe.handler.MessageRequestHandler
import art.openhe.model.request.MessageRequest
import javax.inject.Inject
import javax.inject.Singleton
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("/messages")
@Produces(MediaType.APPLICATION_JSON)
@Singleton
class MessageResource
@Inject constructor(private val handler: MessageRequestHandler): Resource {

    @POST
    fun receiveMessage(request: MessageRequest): Response =
        handler.receiveMessage(request).toResponse()

    @GET
    @Path("/{id}")
    fun getMessage(@PathParam("id") id: String): Response =
        handler.getMessage(id).toResponse()
}
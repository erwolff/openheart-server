package art.openhe.resource

import art.openhe.handler.UserRequestHandler
import art.openhe.model.request.UserRequest
import javax.inject.Inject
import javax.inject.Singleton
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Singleton
class UserResource
@Inject constructor(private val handler: UserRequestHandler): Resource {

    @POST
    fun createUser(request: UserRequest): Response =
        handler.createUser(request).toResponse()

    @GET
    @Path("/{id}")
    fun getUser(@PathParam("id") id: String): Response =
        handler.getUser(id).toResponse()

    @PUT
    @Path("/{id}")
    fun updateUser(@PathParam("id") id: String, request: UserRequest): Response =
        handler.updateUser(id, request).toResponse()

    @DELETE
    @Path("/{id}")
    fun deleteUser(@PathParam("id") id: String): Response =
        handler.deleteUser(id).toResponse()
}
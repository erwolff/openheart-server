package art.openhe.resource

import art.openhe.model.request.UserRequest
import javax.inject.Singleton
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Singleton
class UserResource {

    // TODO: userRequestHandler.getUser(id).toResponse()
    @GET
    @Path("/{id}")
    fun getUser(@PathParam("id") id: String): Response =
        Response.ok().build()

    //TODO: userRequestHandler.createUser(userRequest).toResponse()
    @POST
    fun createUser(userRequest: UserRequest): Response =
        Response.ok().build()

    //TODO: userRequestHandler.updateUser(userRequest).toResponse()
    @PUT
    @Path("/id")
    fun updateUser(@PathParam("id") id: String, userRequest: UserRequest): Response =
        Response.ok().build();
}
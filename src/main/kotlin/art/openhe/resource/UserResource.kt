package art.openhe.resource

import art.openhe.handler.UserRequestHandler
import art.openhe.model.request.UserRequest
import art.openhe.resource.filter.SessionAuthentication
import javax.inject.Inject
import javax.inject.Singleton
import javax.ws.rs.*
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.core.SecurityContext

@Path("/users")
@SessionAuthentication
@Produces(MediaType.APPLICATION_JSON)
@Singleton
class UserResource
@Inject constructor(private val handler: UserRequestHandler): Resource {

    @GET
    fun getUser(@Context securityContext: SecurityContext): Response =
        handler.getUser(securityContext.userPrincipal.name).toResponse()

    @PUT
    fun updateUser(request: UserRequest, @Context securityContext: SecurityContext): Response =
        handler.updateUser(securityContext.userPrincipal.name, request).toResponse()

    @DELETE
    fun deleteUser(@Context securityContext: SecurityContext): Response =
        handler.deleteUser(securityContext.userPrincipal.name).toResponse()
}
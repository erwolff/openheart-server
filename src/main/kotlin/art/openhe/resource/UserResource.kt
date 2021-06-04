package art.openhe.resource

import art.openhe.handler.UserRequestHandler
import art.openhe.model.request.UserRequest
import art.openhe.resource.filter.authentication.SessionAuthentication
import javax.inject.Inject
import javax.inject.Singleton
import javax.ws.rs.*
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.core.SecurityContext

/**
 * REST Resource which handles all User-related requests
 */
@Path("/users")
@SessionAuthentication
@Produces(MediaType.APPLICATION_JSON)
@Singleton
class UserResource
@Inject constructor(
    private val handler: UserRequestHandler
) : Resource {

    /**
     * Returns a UserResponse representing the
     * User object of the calling entity
     */
    @GET
    fun getUser(
        @Context context: SecurityContext
    ): Response =
        handler.getUser(
            id = context.userPrincipal.name
        ).toResponse()


    /**
     * Updates the User object of the calling
     * entity with the supplied UserRequest and
     * returns a UserResponse representing the
     * updated User
     */
    @PUT
    fun updateUser(
        request: UserRequest,
        @Context context: SecurityContext
    ): Response =
        handler.updateUser(
            id = context.userPrincipal.name,
            request = request
        ).toResponse()


    /**
     * Deletes the User object of the calling
     * entity
     */
    @DELETE
    fun deleteUser(
        @Context context: SecurityContext
    ): Response =
        handler.deleteUser(
            id = context.userPrincipal.name
        ).toResponse()

}
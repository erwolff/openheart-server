package art.openhe.resource

import art.openhe.handler.AuthRequestHandler
import art.openhe.model.LoginCredentials
import art.openhe.resource.filter.authentication.FirebaseAuthentication
import art.openhe.resource.filter.authentication.RefreshTokenAuthentication
import javax.inject.Inject
import javax.inject.Singleton
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.core.SecurityContext

/**
 * REST Resource which handles all Auth-related requests
 */
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Singleton
class AuthResource
@Inject constructor(
    private val handler: AuthRequestHandler
) : Resource {

    /**
     * Uses FirebaseAuthentication to login a
     * new/existing User
     */
    @GET
    @Path("/login")
    @FirebaseAuthentication
    fun login(
        @Context context: SecurityContext
    ): Response =
        with(context.userPrincipal.name.split("::")) {
            handler.login(LoginCredentials(
                googleId = this[0],
                email = this[1]
            )).toResponse()
        }


    /**
     * Generates and returns new session/refresh tokens based on the
     * provided security context
     */
    @GET
    @Path("/refresh")
    @RefreshTokenAuthentication
    fun refresh(
        @Context context: SecurityContext
    ): Response =
        handler.refresh(
            userId = context.userPrincipal.name
        ).toResponse()
    
}
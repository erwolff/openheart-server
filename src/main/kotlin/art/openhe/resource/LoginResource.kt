package art.openhe.resource

import art.openhe.handler.LoginRequestHandler
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


@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Singleton
class LoginResource
@Inject constructor(private val handler: LoginRequestHandler): Resource {

    @GET
    @Path("/login")
    @FirebaseAuthentication
    fun login(@Context securityContext: SecurityContext): Response {
        val principle = securityContext.userPrincipal.name.split("::")
        return handler.login(principle[0], principle[1]).toResponse()
    }

    @GET
    @Path("/refresh")
    @RefreshTokenAuthentication
    fun refresh(@Context securityContext: SecurityContext): Response {
        return handler.refresh(securityContext.userPrincipal.name).toResponse()
    }


}
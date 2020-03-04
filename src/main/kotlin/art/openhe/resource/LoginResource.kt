package art.openhe.resource

import art.openhe.handler.LoginRequestHandler
import art.openhe.model.request.LoginRequest
import javax.inject.Inject
import javax.inject.Singleton
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response


@Path("/login")
@Produces(MediaType.APPLICATION_JSON)
@Singleton
class LoginResource
@Inject constructor(private val handler: LoginRequestHandler): Resource {

    @POST
    fun login(request: LoginRequest): Response =
        handler.login(request).toResponse()


}
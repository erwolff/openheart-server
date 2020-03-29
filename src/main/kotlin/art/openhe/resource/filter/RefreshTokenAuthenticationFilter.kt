package art.openhe.resource.filter

import art.openhe.cache.Cache
import art.openhe.util.ext.generateSecurityContext
import io.novocaine.Novocaine
import javax.annotation.Priority
import javax.inject.Inject
import javax.inject.Singleton
import javax.ws.rs.Priorities
import javax.ws.rs.WebApplicationException
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.ContainerRequestFilter
import javax.ws.rs.core.HttpHeaders
import javax.ws.rs.core.Response
import javax.ws.rs.ext.Provider

@RefreshTokenAuthentication
@Singleton
@Provider
@Priority(Priorities.AUTHENTICATION)
class RefreshTokenAuthenticationFilter : ContainerRequestFilter {

    override fun filter(requestContext: ContainerRequestContext?) {
        val refreshToken = requestContext?.getHeaderString(HttpHeaders.AUTHORIZATION)?.replace("Bearer ", "")
            ?: throw WebApplicationException(Response.Status.UNAUTHORIZED)

        val userId = Novocaine.get(Cache::class.java).getUserIdByRefreshToken(refreshToken)
            ?: throw WebApplicationException(Response.Status.UNAUTHORIZED)

        requestContext.generateSecurityContext(userId)
    }
}
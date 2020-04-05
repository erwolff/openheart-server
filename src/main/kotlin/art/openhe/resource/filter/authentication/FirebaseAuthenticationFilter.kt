package art.openhe.resource.filter.authentication

import art.openhe.util.ext.generateSecurityContext
import com.google.firebase.auth.FirebaseAuth
import javax.annotation.Priority
import javax.inject.Singleton
import javax.ws.rs.Priorities
import javax.ws.rs.WebApplicationException
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.ContainerRequestFilter
import javax.ws.rs.core.HttpHeaders
import javax.ws.rs.core.Response
import javax.ws.rs.ext.Provider

/**
 * Ensures the token supplied in the header is a valid firebase auth token
 * Sets userPrinciple to "googleId::email"
 */
@FirebaseAuthentication
@Singleton
@Provider
@Priority(Priorities.AUTHENTICATION)
class FirebaseAuthenticationFilter : ContainerRequestFilter {

    override fun filter(requestContext: ContainerRequestContext?) {
        val googleIdToken = requestContext?.getHeaderString(HttpHeaders.AUTHORIZATION)?.replace("Bearer ", "")
            ?: throw WebApplicationException(Response.Status.UNAUTHORIZED)

        val decodedToken = FirebaseAuth.getInstance().verifyIdToken(googleIdToken)

        requestContext.generateSecurityContext(decodedToken.uid, decodedToken.email)
    }
}
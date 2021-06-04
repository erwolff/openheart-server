package art.openhe.util.ext

import java.nio.file.attribute.UserPrincipal
import java.security.Principal
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.core.SecurityContext


/**
 * Generates the security context with the supplied userId
 * The UserPrincipal will be in form: "userId"
 */
fun ContainerRequestContext.generateSecurityContext(userId: String) {
    this.securityContext = object : SecurityContext {
        override fun getUserPrincipal(): Principal {
            return UserPrincipal { userId }
        }

        override fun isUserInRole(s: String): Boolean {
            return true
        }

        override fun isSecure(): Boolean {
            return securityContext.isSecure
        }

        override fun getAuthenticationScheme(): String {
            return securityContext.authenticationScheme
        }
    }
}

/**
 * Generates the security context with the supplied googleId and email
 * The UserPrincipal will be in form: "googleId::email"
 */
fun ContainerRequestContext.generateSecurityContext(googleId: String, email: String) {
    this.securityContext = object : SecurityContext {
        override fun getUserPrincipal(): Principal {
            return UserPrincipal { "$googleId::$email" }
        }

        override fun isUserInRole(s: String): Boolean {
            return true
        }

        override fun isSecure(): Boolean {
            return securityContext.isSecure
        }

        override fun getAuthenticationScheme(): String {
            return securityContext.authenticationScheme
        }
    }
}
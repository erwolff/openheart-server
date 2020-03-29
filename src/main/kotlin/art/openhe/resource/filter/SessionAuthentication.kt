package art.openhe.resource.filter

import javax.ws.rs.NameBinding


/**
 * Ensures the token supplied in the header is a valid session token
 * Sets userPrinciple to the userId
 */
@NameBinding
@Retention(AnnotationRetention.RUNTIME)
annotation class SessionAuthentication
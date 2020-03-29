package art.openhe.resource.filter

import javax.ws.rs.NameBinding


/**
 * Ensures the token supplied in the header is a valid firebase auth token
 * Sets userPrinciple to "googleId::email"
 */
@NameBinding
@Retention(AnnotationRetention.RUNTIME)
annotation class FirebaseAuthentication
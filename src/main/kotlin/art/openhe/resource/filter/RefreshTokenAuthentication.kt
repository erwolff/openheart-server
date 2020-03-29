package art.openhe.resource.filter

import javax.ws.rs.NameBinding


/**
 * Ensures the token supplied in the header is a valid refresh token
 * Sets userPrinciple to the userId
 */
@NameBinding
@Retention(AnnotationRetention.RUNTIME)
annotation class RefreshTokenAuthentication
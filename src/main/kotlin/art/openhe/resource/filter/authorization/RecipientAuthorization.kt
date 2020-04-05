package art.openhe.resource.filter.authorization

import javax.ws.rs.NameBinding

/**
 * Ensures the userId in the security context is the recipient of the letter
 * with the id supplied in the path
 */
@NameBinding
@Retention(AnnotationRetention.RUNTIME)
annotation class RecipientAuthorization
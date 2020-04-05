package art.openhe.resource.filter.authorization

import javax.ws.rs.NameBinding


/**
 * Ensures the userId in the security context is either the recipient
 * or the author of the letter with the id supplied in the path
 */
@NameBinding
@Retention(AnnotationRetention.RUNTIME)
annotation class AuthorRecipientAuthorization
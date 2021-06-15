package art.openhe.handler

import art.openhe.storage.cache.Cache
import art.openhe.storage.dao.UserDao
import art.openhe.storage.dao.ext.findOne
import art.openhe.model.LoginCredentials
import art.openhe.model.User
import art.openhe.model.ext.updateWith
import art.openhe.model.response.*
import art.openhe.util.RandomUtil
import art.openhe.util.ext.eqCriteria
import javax.inject.Inject
import javax.inject.Singleton
import javax.ws.rs.core.Response


/**
 * Handles all Auth-related operations
 * Interacts with the Storage layers
 * Returns HandlerResult objects to indicate success/failure
 */
@Singleton
class AuthRequestHandler
@Inject constructor(
    private val userDao: UserDao,
    private val cache: Cache
) : Handler {

    /**
     * Creates a brand new user if a user doesn't currently exist with the supplied googleId, then generates
     * a fresh session token for this new or existing user
     */
    fun login(creds: LoginCredentials): HandlerResult<LoginResponse, LoginErrorResponse> =
        processExistingUser(creds) ?: processNewUser(creds)


    /**
     * Generates a fresh session token for the user with the supplied userId
     * Note: refresh token validation has already been applied by the security filter
     */
    fun refresh(userId: String): HandlerResult<SessionTokenResponse, SessionTokenErrorResponse> =
         refreshToken(userId) ?: sessionTokenError


    private fun generateSessionToken(userId: String): SessionTokenResponse =
        with (cache) {
            // end current session
            endSession(userId)

            // generate and return a new token
            return SessionTokenResponse().also {
                setSessionTokenToUserId(it.sessionToken, userId)
                setUserIdToSessionToken(userId, it.sessionToken)
                setRefreshTokenToUserId(it.refreshToken, userId)
                setUserIdToRefreshToken(userId, it.refreshToken)
            }
        }


    // Helper Functions

    private val processExistingUser = { creds: LoginCredentials -> findExistingUserByGoogleId(creds.googleId)?.let { updateEmailIfChanged(it, creds.email) }?.let { successfulLoginResponse(it, false) } }
    private val findExistingUserByGoogleId = { googleId: String ->  userDao.findOne(googleId = googleId.eqCriteria()) }
    private val updateEmailIfChanged = { user: User, email: String -> if (user.email == email) user else userDao.update(user.updateWith("email" to email)) }

    private val processNewUser = { creds: LoginCredentials -> saveNewUser(generateNewUser(creds))?.let { successfulLoginResponse(it, true) } ?: userCreationError }
    private val generateNewUser = { creds: LoginCredentials -> User(email = creds.email, googleId = creds.googleId, avatar = RandomUtil.randomAvatar().name) }
    private val saveNewUser = { user: User -> userDao.save(user) }

    private val successfulLoginResponse = { user: User, firstLogin: Boolean ->  LoginResponse(user.asUserResponse(), generateSessionToken(user.id), firstLogin).asSuccess() }

    private val refreshToken = { userId: String -> userDao.findById(userId)?.let { generateSessionToken(it.id).asSuccess() } }

    companion object {
        private val userCreationError = LoginErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, message = "Unable to create new user").asFailure()
        private val sessionTokenError = SessionTokenErrorResponse(Response.Status.UNAUTHORIZED).asFailure()
    }
}
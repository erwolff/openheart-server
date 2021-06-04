package art.openhe.handler

import art.openhe.cache.Cache
import art.openhe.dao.UserDao
import art.openhe.dao.ext.findOne
import art.openhe.model.User
import art.openhe.model.ext.updateWith
import art.openhe.model.response.*
import art.openhe.util.RandomUtil
import art.openhe.util.ext.eqCriteria
import javax.inject.Inject
import javax.inject.Singleton
import javax.ws.rs.core.Response


@Singleton
class AuthRequestHandler
@Inject constructor(
    private val userDao: UserDao,
    private val cache: Cache
) : Handler {

    fun login(googleId: String, email: String): HandlerResult<LoginResponse, LoginErrorResponse> {
        var firstLogin = false

        // if googleId matches a user in our system, return the user
        var user = userDao.findOne(googleId = googleId.eqCriteria())?.let {
            // this user already exists, ensure that the stored email matches google's email - if not, update
            if (it.email == email) it
            else userDao.update(it.updateWith("email" to email))
        }

        if (user == null) {
            firstLogin = true
            user = userDao.save(
                User(email = email,
                    googleId = googleId,
                    avatar = RandomUtil.randomAvatar().name))

            if (user == null)
                return LoginErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, message = "Unable to create new user").asFailure()
        }

        val token = generateSessionToken(user.id)
        return LoginResponse(user.asUserResponse(), token, firstLogin).asSuccess()
    }

    fun refresh(userId: String): HandlerResult<SessionTokenResponse, SessionTokenErrorResponse> =
        userDao.findById(userId)?.let {
            generateSessionToken(it.id).asSuccess()
        } ?: SessionTokenErrorResponse(Response.Status.UNAUTHORIZED).asFailure()



    private fun generateSessionToken(userId: String): SessionTokenResponse {
        // end current session
        cache.endSession(userId)

        // generate a new token
        val token = SessionTokenResponse()
        cache.setSessionTokenToUserId(token.sessionToken, userId)
        cache.setUserIdToSessionToken(userId, token.sessionToken)
        cache.setRefreshTokenToUserId(token.refreshToken, userId)
        cache.setUserIdToRefreshToken(userId, token.refreshToken)

        return token
    }
}
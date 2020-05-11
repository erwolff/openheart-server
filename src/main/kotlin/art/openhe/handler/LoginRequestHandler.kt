package art.openhe.handler

import art.openhe.cache.Cache
import art.openhe.dao.UserDao
import art.openhe.dao.criteria.StringCriteria
import art.openhe.dao.criteria.StringCriteria.Companion.eq
import art.openhe.dao.ext.findOne
import art.openhe.model.User
import art.openhe.model.response.*
import art.openhe.util.RandomUtil
import art.openhe.util.UpdateQuery
import javax.inject.Inject
import javax.inject.Singleton
import javax.ws.rs.core.Response


@Singleton
class LoginRequestHandler
@Inject constructor(private val userDao: UserDao,
                    private val cache: Cache) {

    fun login(googleId: String, email: String): HandlerResponse {
        var firstLogin = false

        // if googleId matches a user in our system, return the user
        var user = userDao.findOne(googleId = eq(googleId))?.let {
            // this user already exists, ensure that the stored email matches google's email - if not, update
            if (it.email == email) it
            else userDao.update(it.id, UpdateQuery("email" to email))
        }

        if (user == null) {
            firstLogin = true
            user = userDao.save(
                User(email = email,
                    googleId = googleId,
                    avatar = RandomUtil.randomAvatar().name))

            if (user == null)
                return UserErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, message = "Unable to create new user")
        }

        val token = generateSessionToken(user.id)
        return LoginResponse(user.toUserResponse(), token, firstLogin)
    }

    fun refresh(userId: String): HandlerResponse =
        userDao.findById(userId)?.let {
            generateSessionToken(it.id)
        } ?: UserErrorResponse(Response.Status.UNAUTHORIZED)



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
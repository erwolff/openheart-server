package art.openhe.handler

import art.openhe.dao.UserDao
import art.openhe.dao.ext.findOne
import art.openhe.model.User
import art.openhe.model.request.LoginRequest
import art.openhe.model.response.HandlerResponse
import art.openhe.model.response.LoginErrorResponse
import art.openhe.model.response.UserErrorResponse
import art.openhe.model.response.toUserResponse
import art.openhe.util.RandomUtil
import art.openhe.util.UpdateQuery
import art.openhe.util.ext.letIfNotEmpty
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject
import javax.inject.Singleton
import javax.ws.rs.core.Response


@Singleton
class LoginRequestHandler
@Inject constructor(private val userDao: UserDao) {

    fun login(request: LoginRequest): HandlerResponse {
        // decode token supplied by client
        val decodedToken = request.googleIdToken?.letIfNotEmpty {
            FirebaseAuth.getInstance().verifyIdToken(it)
        } ?: return LoginErrorResponse(Response.Status.UNAUTHORIZED, googleIdToken = "Invalid googleIdToken supplied")

        val googleId = decodedToken.uid
        val email = decodedToken.email

        // if googleId matches a user in our system, return the user
        return userDao.findOne(googleId)?.let {
            // this user already exists, ensure that the stored email matches google's email - if not, update
            if (it.email == decodedToken.email) it.toUserResponse()
            else userDao.update(it.id, UpdateQuery("email" to decodedToken.email))?.toUserResponse()
                ?: UserErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, message = "Unable to update user's email")
        }
            // create a new user and return
            ?: email?.let {
                userDao.save(
                    User(
                        email = it,
                        googleId = googleId,
                        avatar = RandomUtil.randomAvatar().name
                    )
                )
            }?.toUserResponse(firstLogin = true)

            ?: UserErrorResponse(Response.Status.INTERNAL_SERVER_ERROR,message = "Unable to create new user")
    }
}
package art.openhe.handler

import art.openhe.dao.UserDao
import art.openhe.dao.ext.findByGoogleId
import art.openhe.model.User
import art.openhe.model.response.HandlerResponse
import art.openhe.model.response.UserErrorResponse
import art.openhe.model.response.toUserResponse
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject
import javax.inject.Singleton
import javax.ws.rs.core.Response


@Singleton
class LoginRequestHandler
@Inject constructor(private val userDao: UserDao) {


    //TODO: put idToken in a request object which is POSTed to the LoginResource
    fun login(idToken: String): HandlerResponse {
        // decode token supplied by client
        val decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken)
        val googleId = decodedToken.uid
        val email = decodedToken.email

        // if googleId matches a user in our system, return the user
        // TODO: Verify email of user matches email from token - update stored user if mismatch
        return userDao.findByGoogleId(googleId)?.toUserResponse()

            // otherwise create a new user and return
            ?: email?.let {
                userDao.save(
                    User(
                        email = it,
                        googleId = googleId,
                        avatar = "Sloth") //TODO: Generate a random avatar
                )
            }?.toUserResponse()

                    ?: UserErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, message = "Unable to create new user")

    }
}
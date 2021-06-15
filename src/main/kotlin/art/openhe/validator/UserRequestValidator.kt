package art.openhe.validator

import art.openhe.storage.dao.UserDao
import art.openhe.model.Avatar
import art.openhe.model.request.UserRequest
import art.openhe.model.response.UserErrorResponse
import art.openhe.util.ext.invalid
import javax.inject.Inject
import javax.inject.Singleton
import javax.ws.rs.core.Response


/**
 * Validates UserRequests
 */
@Singleton
class UserRequestValidator
@Inject constructor(
    private val userDao: UserDao
) : Validator {

    /**
     * Validates the provided id and UserRequest ensuring that:
     * - a user with this id exists
     * - the 'avatar' in the request is valid (or null)
     */
    fun validateUpdate(
        id: String,
        request: UserRequest
    ): UserErrorResponse? =
        with(request) {
            userDao.findById(id)
                ?: return userNotFound(id)

            if (avatar?.invalid<Avatar>() == true)
                return invalidAvatar(avatar)

            return valid
        }


    companion object {
        val userNotFound = { id: String -> UserErrorResponse(Response.Status.NOT_FOUND, id = "A user with id $id does not exist") }
        val invalidAvatar = { avatar: String -> UserErrorResponse(Response.Status.BAD_REQUEST, avatar = "Invalid avatar: $avatar") }
    }

}
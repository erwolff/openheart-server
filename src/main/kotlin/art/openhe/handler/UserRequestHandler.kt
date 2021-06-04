package art.openhe.handler

import art.openhe.dao.UserDao
import art.openhe.model.request.LetterRequest
import art.openhe.model.request.UserRequest
import art.openhe.model.response.*
import art.openhe.util.DbUpdate
import art.openhe.validator.UserRequestValidator
import javax.inject.Inject
import javax.inject.Singleton
import javax.ws.rs.core.Response

/**
 * Handles all UserRequest-related operations
 * Interacts with the Validator and Dao layers
 * Returns HandlerResult objects to indicate success/failure
 */
@Singleton
class UserRequestHandler
@Inject constructor(
    private val validator: UserRequestValidator,
    private val userDao: UserDao
) : Handler {

    /**
     * Retrieves the User with the supplied id
     *
     * Success: Returns the User as a UserResponse
     * Failure: Returns a UserErrorResponse with status NOT_FOUND
     */
    fun getUser(
        id: String
    ): HandlerResult<UserResponse, UserErrorResponse> =
        findUser(id)?.asUserResponse()?.asSuccess()
            ?: userNotFound(id)


    /**
     * Validates and updates the User with the supplied
     * id, and returns the newly updated User as a UserResponse
     *
     * Success: Returns the updated User as a UserResponse
     * Failure: Returns a UserErrorResponse with status BAD_REQUEST or NOT_FOUND
     */
    fun updateUser(
        id: String,
        request: UserRequest
    ): HandlerResult<UserResponse, UserErrorResponse> =
        with(request) {
            validateUpdate(id, this)?.let { return it.asFailure() }

            return toUpdateQuery(id)
                .let(updateUser)?.asUserResponse()?.asSuccess()
                ?: userNotFound(id)
        }

    /**
     * Deletes the User with the supplied id
     *
     * Success: Returns an EmptyResponse
     * Failure: Returns a UserErrorResponse with status NOT_FOUND
     */
    fun deleteUser(
        id: String
    ): HandlerResult<EmptyResponse, UserErrorResponse> =
        if (delUser(id)) emptyResponse
        else userNotFound(id)



    // Helper Functions

    private val validateUpdate = { id: String, request: UserRequest -> validator.validateUpdate(id, request) }
    private val findUser = { id: String -> userDao.findById(id) }
    private val updateUser = { dbUpdate: DbUpdate -> userDao.update(dbUpdate) }
    private val delUser = { id: String -> userDao.delete(id) > 0 }

    companion object {
        val userNotFound = { id: String -> UserErrorResponse(Response.Status.NOT_FOUND, id = "A user with id $id does not exist").asFailure() }
    }
}
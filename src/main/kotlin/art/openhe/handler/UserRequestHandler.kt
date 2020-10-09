package art.openhe.handler

import art.openhe.dao.UserDao
import art.openhe.model.request.UserRequest
import art.openhe.model.response.*
import javax.inject.Inject
import javax.inject.Singleton
import javax.ws.rs.core.Response

@Singleton
class UserRequestHandler
@Inject constructor(
    private val userDao: UserDao
) {

    fun updateUser(id: String, request: UserRequest): HandlerResult<UserResponse, UserErrorResponse> =
        //TODO: Validation
        userDao.update(request.toUpdateQuery(id))?.asUserResponse()?.asSuccess()
            ?: UserErrorResponse(Response.Status.NOT_FOUND, id = "A user with id $id does not exist").asFailure()


    fun deleteUser(id: String): HandlerResult<EmptyResponse, UserErrorResponse> =
        userDao.delete(id)?.let { EmptyResponse() }?.asSuccess()
            ?: UserErrorResponse(Response.Status.NOT_FOUND, id = "A user with id $id does not exist").asFailure()


    fun getUser(id: String): HandlerResult<UserResponse, UserErrorResponse> =
        userDao.findById(id)?.asUserResponse()?.asSuccess()
            ?: UserErrorResponse(Response.Status.NOT_FOUND, id = "A user with id $id does not exist").asFailure()
}
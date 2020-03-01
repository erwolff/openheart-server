package art.openhe.handler

import art.openhe.dao.UserDao
import art.openhe.dao.ext.findByEmail
import art.openhe.model.request.UserRequest
import art.openhe.model.response.*
import javax.inject.Inject
import javax.inject.Singleton
import javax.ws.rs.core.Response

@Singleton
class UserRequestHandler
@Inject constructor(private val userDao: UserDao) {


    fun createUser(request: UserRequest): ApiResponse<UserResponse, UserErrorResponse> =
        userDao.save(request.applyAsSave())?.toUserResponse()?.toApiResponse()
            ?: UserErrorResponse(Response.Status.CONFLICT, email = "A user with email ${request.email} already exists").toApiResponse()


    fun updateUser(id: String, request: UserRequest): ApiResponse<UserResponse, UserErrorResponse> =
        if (request.email != null && userDao.findByEmail(request.email) != null)
            UserErrorResponse(Response.Status.CONFLICT, email = "A user with email ${request.email} already exists").toApiResponse()

        else userDao.update(id, request.toUpdateQuery())?.toUserResponse()?.toApiResponse()
            ?: UserErrorResponse(Response.Status.NOT_FOUND, id = "A user with id $id does not exist").toApiResponse()


    fun deleteUser(id: String): ApiResponse<EmptyResponse, UserErrorResponse> =
        userDao.delete(id)?.let { EmptyResponse() }?.toApiResponse()
            ?: UserErrorResponse(Response.Status.NOT_FOUND, id = "A user with id $id does not exist").toApiResponse()


    fun getUser(id: String): ApiResponse<UserResponse, UserErrorResponse> =
        userDao.findById(id)?.toUserResponse()?.toApiResponse()
            ?: UserErrorResponse(Response.Status.NOT_FOUND, id = "A user with id $id does not exist").toApiResponse()
}
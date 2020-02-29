package art.openhe.handler

import art.openhe.dao.UserDao
import art.openhe.model.request.UserRequest
import art.openhe.model.response.*
import javax.inject.Inject
import javax.inject.Singleton
import javax.ws.rs.core.Response

@Singleton
class UserRequestHandler
@Inject constructor(private val userDao: UserDao) {


    fun getUser(id: String): ApiResponse<UserResponse, UserErrorResponse> =
        userDao.findById(id)?.toUserResponse()?.toApiResponse()
            ?: UserErrorResponse(Response.Status.NOT_FOUND, id = "A user with id $id does not exist").toApiResponse()


    fun createUser(request: UserRequest): ApiResponse<UserResponse, UserErrorResponse> =
        userDao.save(request.applyAsSave())?.toUserResponse()?.toApiResponse()
            ?: UserErrorResponse(Response.Status.CONFLICT, email = "A user with email ${request.email} already exists").toApiResponse()


    fun updateUser(id: String, request: UserRequest): ApiResponse<UserResponse, UserErrorResponse> {
        val user = userDao.findById(id)
            ?: return UserErrorResponse(Response.Status.NOT_FOUND, id = "A user with id $id does not exist").toApiResponse()

        return userDao.save(request.applyAsUpdate(user))?.toUserResponse()?.toApiResponse()
            ?: UserErrorResponse(Response.Status.CONFLICT).toApiResponse()
    }


    fun deleteUser(id: String): ApiResponse<EmptyResponse, UserErrorResponse> {
        val user = userDao.findById(id)
            ?: return UserErrorResponse(Response.Status.NOT_FOUND, id = "A user with id $id does not exist").toApiResponse()

        userDao.delete(user.id)
        return EmptyResponse().toApiResponse()
    }

}
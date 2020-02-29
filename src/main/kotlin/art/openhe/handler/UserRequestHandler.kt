package art.openhe.handler

import art.openhe.service.UserService
import art.openhe.model.request.UserRequest
import art.openhe.model.response.*
import javax.inject.Inject
import javax.inject.Singleton
import javax.ws.rs.core.Response

@Singleton
class UserRequestHandler
@Inject constructor(private val userService: UserService) {


    fun getUser(id: String): ApiResponse<UserResponse, UserErrorResponse> =
        userService.findById(id)?.toUserResponse()?.toApiResponse()
            ?: UserErrorResponse(Response.Status.NOT_FOUND, id = "A user with id $id does not exist").toApiResponse()


    fun createUser(request: UserRequest): ApiResponse<UserResponse, UserErrorResponse> =
        userService.save(request.applyAsSave())?.toUserResponse()?.toApiResponse()
            ?: UserErrorResponse(Response.Status.CONFLICT, email = "A user with email ${request.email} already exists").toApiResponse()


    fun updateUser(id: String, request: UserRequest): ApiResponse<UserResponse, UserErrorResponse> {
        val user = userService.findById(id)
            ?: return UserErrorResponse(Response.Status.NOT_FOUND, id = "A user with id $id does not exist").toApiResponse()

        return userService.save(request.applyAsUpdate(user))?.toUserResponse()?.toApiResponse()
            ?: UserErrorResponse(Response.Status.CONFLICT).toApiResponse()
    }


    fun deleteUser(id: String): ApiResponse<EmptyResponse, UserErrorResponse> {
        val user = userService.findById(id)
            ?: return UserErrorResponse(Response.Status.NOT_FOUND, id = "A user with id $id does not exist").toApiResponse()

        userService.delete(user.id)
        return EmptyResponse().toApiResponse()
    }

}
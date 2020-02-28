package art.openhe.handler

import art.openhe.model.User
import art.openhe.model.request.UserRequest
import art.openhe.model.response.*
import javax.inject.Singleton

@Singleton
class UserRequestHandler {

    fun getUser(id: String): ApiResponse<UserResponse, UserErrorResponse> =
        User(id, "testing@example.com", "Sloth").toUserResponse().toApiResponse()

    fun createUser(request: UserRequest): ApiResponse<UserResponse, UserErrorResponse> =
        User("abc123", request.email, request.avatar).toUserResponse().toApiResponse()

    fun updateUser(id: String, request: UserRequest): ApiResponse<UserResponse, UserErrorResponse> =
        request.applyAsUpdate(User("abc123", "testing@example.com", "Sloth")).toUserResponse().toApiResponse()

    fun deleteUser(id: String): ApiResponse<EmptyResponse, UserErrorResponse> =
        EmptyResponse().toApiResponse()
}
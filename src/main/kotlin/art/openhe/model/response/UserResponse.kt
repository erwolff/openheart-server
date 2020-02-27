package art.openhe.model.response

import art.openhe.model.User


data class UserResponse (

    val id: String?,
    val email: String?,
    val avatar: String?

) : EntityResponse()

fun User.toUserResponse() =
    UserResponse(id, email, avatar)
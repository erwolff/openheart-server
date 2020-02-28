package art.openhe.model.response

import art.openhe.model.User


data class UserResponse (

    val id: String? = null,
    val email: String? = null,
    val avatar: String? = null

) : EntityResponse()

fun User.toUserResponse() =
    UserResponse(id,
        email,
        avatar)
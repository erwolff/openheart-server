package art.openhe.model.response

import art.openhe.model.User


data class UserResponse (

    val id: String? = null,
    val email: String? = null,
    val avatar: String? = null,
    val lastSentMessageTimestamp: Long = 0,
    val lastReceivedMessageTimestamp: Long = 0,
    val numHearts: Long = 0

) : EntityResponse()

fun User.toUserResponse() =
    UserResponse(id,
        email,
        avatar,
        lastSentMessageTimestamp,
        lastReceivedMessageTimestamp,
        numHearts)
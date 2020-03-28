package art.openhe.model.response

import art.openhe.model.User


data class UserResponse (

    val id: String? = null,
    val avatar: String? = null,
    val lastSentLetterTimestamp: Long = 0,
    val lastReceivedLetterTimestamp: Long = 0,
    val hearts: Long = 0,
    val firstLogin: Boolean? = false

) : EntityResponse()

fun User.toUserResponse(firstLogin: Boolean? = false) =
    UserResponse(id,
        avatar,
        lastSentLetterTimestamp,
        lastReceivedLetterTimestamp,
        hearts,
        firstLogin)
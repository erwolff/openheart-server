package art.openhe.model.response

import art.openhe.model.User


data class UserResponse (

    val id: String? = null,
    val avatar: String? = null,
    val lastSentLetterTimestamp: Long = 0,
    val lastReceivedLetterTimestamp: Long = 0,
    val hearts: Long = 0

) : EntityResponse

fun User.asUserResponse() =
    UserResponse(id,
        avatar,
        lastSentLetterTimestamp,
        lastReceivedLetterTimestamp,
        hearts)
package art.openhe.storage.cache


object CacheKey {
    const val userIdString = "{userId}"
    const val messageIdString = "{messageId}"
    const val tokenString = "{token}"

    const val sessionToken = "session:$tokenString"
    const val refreshToken = "refresh:$tokenString"
    const val sessionUserId = "session_user_id:$userIdString"
    const val refreshUserId = "refresh_user_id:$userIdString"
    const val sqsMessageId = "sqs_message_id:$messageIdString"
}
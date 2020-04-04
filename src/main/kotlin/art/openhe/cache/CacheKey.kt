package art.openhe.cache


object CacheKey {
    val userIdString = "{userId}"
    val messageIdString = "{messageId}"
    val tokenString = "{token}"

    val sessionToken = "session:$tokenString"
    val refreshToken = "refresh:$tokenString"
    val sessionUserId = "session_user_id:$userIdString"
    val refreshUserId = "refresh_user_id:$userIdString"
    val sqsMessageId = "sqs_message_id:$messageIdString"
}
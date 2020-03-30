package art.openhe.cache


object CacheKey {
    val userIdString = "{userId}"
    val tokenString = "{token}"
    val sessionToken = "session:$tokenString"
    val refreshToken = "refresh:$tokenString"
    val sessionUserId = "session_user_id:$userIdString"
    val refreshUserId = "refresh_user_id:$userIdString"
}
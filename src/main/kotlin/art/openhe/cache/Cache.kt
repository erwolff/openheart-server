package art.openhe.cache

import org.apache.commons.lang3.StringUtils
import redis.clients.jedis.params.SetParams
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Cache
@Inject constructor(private val jedisRetryer: JedisRetryer) {

    companion object {
        val defaultExpSeconds = TimeUnit.MINUTES.toSeconds(30).toInt()
        val sessionTokenExpSeconds = TimeUnit.DAYS.toSeconds(2).toInt()
        val refreshTokenExpSeconds = TimeUnit.DAYS.toSeconds(90).toInt()
    }

    fun setSessionTokenToUserId(sessionToken: String, userId: String) =
        setNX(StringUtils.replace(CacheKey.sessionToken, CacheKey.tokenString, sessionToken), userId, sessionTokenExpSeconds)

    fun getUserIdBySessionToken(sessionToken: String) =
        get(StringUtils.replace(CacheKey.sessionToken, CacheKey.tokenString, sessionToken))


    fun setUserIdToSessionToken(userId: String, sessionToken: String) =
        setNX(StringUtils.replace(CacheKey.sessionUserId, CacheKey.userIdString, userId), sessionToken, sessionTokenExpSeconds)

    fun getSessionTokenByUserId(userId: String) =
        get(StringUtils.replace(CacheKey.sessionUserId, CacheKey.userIdString, userId))


    fun setRefreshTokenToUserId(refreshToken: String, userId: String) =
        setNX(StringUtils.replace(CacheKey.refreshToken, CacheKey.tokenString, refreshToken), userId, refreshTokenExpSeconds)

    fun getUserIdByRefreshToken(refreshToken: String) =
        get(StringUtils.replace(CacheKey.refreshToken, CacheKey.tokenString, refreshToken))


    fun endSession(userId: String? = null, sessionToken: String? = null) {
        if (userId == null && sessionToken == null) return

        val sToken = sessionToken ?: getSessionTokenByUserId(userId!!)
        val id = userId ?: getUserIdBySessionToken(sessionToken!!)

        del(StringUtils.replace(CacheKey.sessionUserId, CacheKey.userIdString, id))
        del(StringUtils.replace(CacheKey.sessionToken, CacheKey.tokenString, sToken))
    }

    private fun set(key: String, value:String, expSeconds: Int = defaultExpSeconds) {
        jedisRetryer.withString { it.setex(key, expSeconds, value) }
    }

    private fun setNX(key: String, value: String, expSeconds: Int = defaultExpSeconds): Boolean =
        "OK" == jedisRetryer.withString { it.set(key, value, SetParams.setParams().ex(expSeconds).nx()) }

    private fun get(key: String) = jedisRetryer.withString { it.get(key) }

    private fun del(key: String) = jedisRetryer.withLong { it.del(key) }


}
package art.openhe.model.response

import art.openhe.cache.Cache
import org.joda.time.DateTimeUtils
import java.util.*
import java.util.concurrent.TimeUnit


data class SessionTokenResponse (

    val sessionToken: String = UUID.randomUUID().toString(),
    val refreshToken: String = UUID.randomUUID().toString(),
    val expirationTimestamp: Long =
        DateTimeUtils.currentTimeMillis() + TimeUnit.SECONDS.toMillis(Cache.sessionTokenExpSeconds.toLong())

) : EntityResponse()
package art.openhe.storage.cache

import art.openhe.util.logError
import com.github.rholder.retry.RetryerBuilder
import com.github.rholder.retry.StopStrategies
import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class JedisRetryer
@Inject constructor(
    @Named("jedisPool") private val jedisPool: JedisPool
) {

    private val numRetries = 4

    private val stringRetryer = RetryerBuilder.newBuilder<String>()
        .retryIfException()
        .withStopStrategy(StopStrategies.stopAfterAttempt(numRetries))
        .build()

    private val longRetryer = RetryerBuilder.newBuilder<Long>()
        .retryIfException()
        .withStopStrategy(StopStrategies.stopAfterAttempt(numRetries))
        .build()

    fun withString(lambda: (Jedis) -> String?): String? =
        try {
            jedisPool.resource.use {
                stringRetryer.call {
                    lambda(it)
                }
            }
        } catch (e: Exception) {
            logError(e) { "redis retry failed" }
            throw RuntimeException(e)
        }

    fun withLong(lambda: (Jedis) -> Long): Long =
        try {
            jedisPool.resource.use {
                longRetryer.call {
                    lambda(it)
                }
            }
        } catch (e: Exception) {
            logError(e) { "redis retry failed" }
            throw RuntimeException(e)
        }


}
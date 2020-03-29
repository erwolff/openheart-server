package art.openhe.cache

import art.openhe.config.EnvConfig
import art.openhe.util.logger
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig
import redis.clients.jedis.util.JedisURIHelper
import java.net.URI
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

class CacheProvider {

    private val CONNECTION_TIMEOUT = 2000
    private val SOCKET_TIMEOUT = 2000

    private val log = logger()
    private var jedisPool: JedisPool? = null

    @Singleton
    @Named("jedisPool")
    fun getJedisPool(envConfig: EnvConfig): JedisPool {
        if (jedisPool == null) {
            init(envConfig)
        }
        return jedisPool as JedisPool
    }

    private fun init(envConfig: EnvConfig) {
        log.info("Initializing JedisPool")
        val config = JedisPoolConfig()
        config.maxTotal = 4

        val redisUrl = URI(envConfig.redisUrl())

        jedisPool = JedisPool(config,
            redisUrl.host,
            redisUrl.port,
            CONNECTION_TIMEOUT,
            SOCKET_TIMEOUT,
            JedisURIHelper.getPassword(redisUrl),
            JedisURIHelper.getDBIndex(redisUrl),
            "openheart")
    }
}
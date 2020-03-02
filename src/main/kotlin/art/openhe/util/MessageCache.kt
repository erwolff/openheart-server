package art.openhe.util

import java.util.*

/**
 * Temporary in-memory cache for deduping SQS messages
 * TODO: Replace with a caching solution (e.g. redis)
 */
object MessageCache {

    private val maxCacheSize = 100
    private val cache = LinkedList<String>()

    fun isDuplicate(messageId: String): Boolean {
        synchronized(cache) {
            if (cache.remove(messageId)) { // cache contained this messageId
                cache.addFirst(messageId)
                return true
            } else { // cache did not contain the value
                cache.addFirst(messageId)
                if (cache.size > maxCacheSize) { // cache has exceeded our max size
                    // remove the last value
                    cache.removeLast()
                }
            }
            return false
        }
    }
}
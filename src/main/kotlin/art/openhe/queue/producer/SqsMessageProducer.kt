package art.openhe.queue.producer

import art.openhe.queue.QueueManager
import art.openhe.util.ext.toJsonString
import art.openhe.util.logger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SqsMessageProducer
@Inject constructor(private val queueManager: QueueManager) {

    private val log = logger()

    fun publish(message: Any, queue: String) {
        try {
            queueManager.publish(queue, message.toJsonString())
        } catch (e: Exception) {
            log.error("Error publishing message: $message", e)
        }
    }
}
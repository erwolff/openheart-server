package art.openhe.queue.producer

import art.openhe.queue.QueueProvider
import art.openhe.util.ext.serialize
import art.openhe.util.logger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SqsMessageProducer
@Inject constructor(private val queueProvider: QueueProvider) {

    private val log = logger()

    fun publish(message: Any, queue: String) {
        try {
            queueProvider.publish(queue, message.serialize())
        } catch (e: Exception) {
            log.error("Error publishing message: $message", e)
        }
    }
}
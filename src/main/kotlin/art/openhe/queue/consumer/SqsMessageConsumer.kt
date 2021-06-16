package art.openhe.queue.consumer

import art.openhe.storage.cache.Cache
import art.openhe.queue.QueueProvider
import art.openhe.util.logError
import javax.inject.Inject
import javax.jms.Message
import javax.jms.MessageListener
import javax.jms.TextMessage


abstract class SqsMessageConsumer : MessageListener {

    @Inject private lateinit var queueProvider: QueueProvider
    @Inject private lateinit var cache: Cache

    abstract val queueName: String

    fun register() {
        queueProvider.registerConsumer(queueName, this)
    }

    override fun onMessage(message: Message?) {
        try {
            message?.let {
                if (!cache.isDuplicateSqsMessage(it.jmsMessageID)) {
                    onMessage((it as TextMessage).text)
                }
            }
        } catch (e: Exception) {
            logError(e) { "Error processing message: ${message.toString()}" }
        } finally {
            try {
                message?.acknowledge()
            } catch (e: Exception) {
                logError(e) { "Error acknowledging message: ${message.toString()}" }
            }
        }
    }

    abstract fun onMessage(message: String)
}
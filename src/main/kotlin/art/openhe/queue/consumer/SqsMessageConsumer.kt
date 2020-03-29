package art.openhe.queue.consumer

import art.openhe.queue.QueueProvider
import art.openhe.queue.ext.letIfNotDuplicate
import art.openhe.util.logger
import javax.inject.Inject
import javax.jms.Message
import javax.jms.MessageListener
import javax.jms.TextMessage


abstract class SqsMessageConsumer : MessageListener {

    @Inject private lateinit var queueProvider: QueueProvider

    abstract val queueName: String

    private val log = logger()

    fun register() {
        queueProvider.registerConsumer(queueName, this)
    }

    override fun onMessage(message: Message?) {
        try {
            message?.letIfNotDuplicate {
                onMessage((it as TextMessage).text)
            }
        } catch (e: Exception) {
            log.error("Error processing message: ${message.toString()}", e)
        } finally {
            try {
                message?.acknowledge()
            } catch (e: Exception) {
                log.error("Error acknowledging message: ${message.toString()}", e)
            }
        }
    }

    abstract fun onMessage(message: String)
}
package art.openhe.queue.consumer

import art.openhe.brains.MessageProcessor
import art.openhe.model.Message
import art.openhe.queue.Queues
import art.openhe.util.ext.mapTo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessageProcessorConsumer
@Inject constructor(private val messageProcessor: MessageProcessor) : SqsMessageConsumer() {

    override val queueName: String = Queues.messageProcessor

    override fun onMessage(message: String) {
        message.mapTo(Message::class.java)?.let {
            messageProcessor.process(it)
        }
    }
}
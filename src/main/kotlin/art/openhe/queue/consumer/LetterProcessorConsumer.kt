package art.openhe.queue.consumer

import art.openhe.brains.LetterProcessor
import art.openhe.model.Letter
import art.openhe.queue.Queues
import art.openhe.util.ext.mapTo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LetterProcessorConsumer
@Inject constructor(private val letterProcessor: LetterProcessor) : SqsMessageConsumer() {

    override val queueName: String = Queues.letterProcessor

    override fun onMessage(message: String) {
        message.mapTo(Letter::class.java)?.let {
            letterProcessor.process(it)
        }
    }
}
package art.openhe.queue.consumer

import art.openhe.brains.LetterPreviewer
import art.openhe.model.Letter
import art.openhe.queue.Queues
import art.openhe.util.ext.deserializeAs
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LetterPreviewerConsumer
@Inject constructor(
    private val letterPreviewer: LetterPreviewer
) : SqsMessageConsumer() {

    override val queueName: String = Queues.letterPreviewer

    override fun onMessage(message: String) {
        message.deserializeAs(Letter::class.java)?.let {
            letterPreviewer.process(it)
        }
    }
}
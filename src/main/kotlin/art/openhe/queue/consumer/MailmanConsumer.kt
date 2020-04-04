package art.openhe.queue.consumer

import art.openhe.brains.Mailman
import art.openhe.model.Letter
import art.openhe.queue.Queues
import art.openhe.util.ext.mapTo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MailmanConsumer
@Inject constructor(private val mailman: Mailman) : SqsMessageConsumer() {

    override val queueName: String = Queues.mailman

    override fun onMessage(message: String) {
        message.mapTo(Letter::class.java)?.let {
            mailman.mail(it)
        }
    }
}
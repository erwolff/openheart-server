package art.openhe.brains

import art.openhe.dao.MessageDao
import art.openhe.model.Message
import art.openhe.util.logger
import org.joda.time.DateTimeUtils
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Receives new messages,
 * finds recipients,
 * sends the message to the recipients,
 * stores the message
 */
@Singleton
class MessageProcessor
@Inject constructor(
    private val messageDao: MessageDao,
    private val recipientFinder: RecipientFinder
) {

    private val log = logger()

    fun process(message: Message) {
        val messageToSend =
            if (message.isReply) message
            else findRecipient(message)

        if (messageToSend.recipientId == null) {
            log.error("Unable to find recipient for message from author ${message.authorId}")
            storeMessage(message)
            return
        }

        // send message to recipient
        val sentTimestamp = sendMessage(messageToSend)

        // store the sent message
        storeMessage(messageToSend.update(sentTimestamp = sentTimestamp))
    }

    private fun findRecipient(message: Message): Message {
        log.info("Finding recipient for message from author: ${message.authorId}")

        return recipientFinder.find(message)?.let {
            message.update(recipientId = it.id, recipientAvatar = it.avatar)
        } ?: message
    }

    private fun sendMessage(message: Message): Long {
        log.info("Sending message from author ${message.authorId} to recipient ${message.recipientId}")

        // send message to recipientId
        //TODO

        //return sentTimestamp
        return DateTimeUtils.currentTimeMillis()
    }

    private fun storeMessage(message: Message) {
        log.info("Storing message from author ${message.authorId}")
        messageDao.save(message)
    }
}
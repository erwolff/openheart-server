package art.openhe.brains

import art.openhe.dao.MessageDao
import art.openhe.model.Message
import art.openhe.util.UpdateQuery
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
        val recipient =
            if(message.isReply) message.recipientId to message.recipientAvatar
            else findRecipient(message)

        val recipientId = recipient?.first
        val recipientAvatar = recipient?.second

        if (recipient == null || recipientId == null || recipientAvatar == null) {
            log.error("Unable to find recipient for message from author ${message.authorId}")
            return
        }

        // send message to recipient
        val sentTimestamp = sendMessage(message, recipientId, recipientAvatar)

        // update the message with the sentTimestamp and recipient
        updateMessage(message, sentTimestamp, recipientId, recipientAvatar)
    }

    private fun findRecipient(message: Message): Pair<String, String>? {
        log.info("Finding recipient for message from author: ${message.authorId}")

        return  recipientFinder.find(message)?.let {
            it.id to it.avatar
        }
    }

    private fun sendMessage(message: Message, recipientId: String, recipientAvatar: String): Long {
        log.info("Sending message from author ${message.authorId} to recipient $recipientId")

        // send message to recipientId
        //TODO

        //return sentTimestamp
        return DateTimeUtils.currentTimeMillis()
    }

    private fun updateMessage(message: Message, sentTimestamp: Long, recipientId: String, recipientAvatar: String) {
        log.info("Updating message from author ${message.authorId}")

        messageDao.update(message.id, UpdateQuery(
            "sentTimestamp" to sentTimestamp,
            "recipientId" to recipientId,
            "recipientAvatar" to recipientAvatar))
    }
}
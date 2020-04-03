package art.openhe.brains

import art.openhe.dao.LetterDao
import art.openhe.dao.UserDao
import art.openhe.model.Letter
import art.openhe.util.UpdateQuery
import art.openhe.util.logger
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import org.apache.commons.lang3.StringUtils
import org.joda.time.DateTimeUtils
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Receives new letters,
 * finds recipient,
 * sends the letter to the recipient,
 * stores the letter
 */
@Singleton
class LetterProcessor
@Inject constructor(private val letterDao: LetterDao,
                    private val userDao: UserDao,
                    private val recipientFinder: RecipientFinder) {

    private val notificationTxt = "A letter has found its way to you!";
    private val notificationReplyTxt = "You've received a reply to your letter!";

    private val log = logger()

    fun process(letter: Letter) {
        val isReply = StringUtils.isNotBlank(letter.parentId)
        val recipient =
            if(isReply) letter.recipientId to letter.recipientAvatar
            else findRecipient(letter)

        val recipientId = recipient?.first
        val recipientAvatar = recipient?.second

        if (recipient == null || recipientId == null || recipientAvatar == null) {
            log.error("Unable to find recipient for letter from author ${letter.authorId}")
            return
        }

        if (isReply) {
            // this is a reply, we need to update the original letter
            updateOriginalLetter(letter.parentId!!, letter.id)
        }

        log.info("Sending letter from author ${letter.authorId} to recipient $recipientId")

        // update author's lastSentLetterTimestamp
        updateAuthor(letter.authorId)

        // update the recipient's lastReceivedLetterTimestamp - but only if this is not a reply
        if (!isReply) updateRecipient(recipientId)

        // update the letter with the sentTimestamp and recipient
        updateLetter(letter, recipientId, recipientAvatar)

        // send push notification to recipient
        notifyRecipient(letter.id, recipientId, recipientAvatar, isReply)
    }

    private fun findRecipient(letter: Letter): Pair<String, String>? {
        log.info("Finding recipient for letter from author ${letter.authorId}")

        return  recipientFinder.find(letter)?.let {
            it.id to it.avatar
        }
    }

    private fun updateOriginalLetter(originalLetterId: String, childId: String) =
        letterDao.update(originalLetterId, UpdateQuery(
            "childId" to childId))

    private fun updateAuthor(authorId: String) =
        // update author's lastSentLetterTimestamp
        userDao.update(authorId, UpdateQuery("lastSentLetterTimestamp" to DateTimeUtils.currentTimeMillis()))

    private fun updateRecipient(recipientId: String) =
        // update recipient's lastReceivedLetterTimestamp
        userDao.update(recipientId, UpdateQuery("lastReceivedLetterTimestamp" to DateTimeUtils.currentTimeMillis()))

    private fun updateLetter(letter: Letter, recipientId: String, recipientAvatar: String) =
        letterDao.update(letter.id, UpdateQuery(
            "sentTimestamp" to DateTimeUtils.currentTimeMillis(),
            "recipientId" to recipientId,
            "recipientAvatar" to recipientAvatar))

    private fun notifyRecipient(letterId: String, recipientId: String, recipientAvatar: String, isReply: Boolean) {
        val message = Message.builder().setNotification(
                Notification.builder()
                    .setTitle("Dear $recipientAvatar")
                    .setBody(if (isReply) notificationReplyTxt else notificationTxt)
                    .build())
            .putData("click_action", "FLUTTER_NOTIFICATION_CLICK")
            .putData("letterId", letterId)
            .setTopic(recipientId)
            .build()

        val response = FirebaseMessaging.getInstance().send(message)
        log.info("Sent notification to recipient $recipientId - messageId: $response")
    }

}
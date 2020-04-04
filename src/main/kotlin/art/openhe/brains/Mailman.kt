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
 * updates the parent letter (adding as child)
 * updates author's lastSentLetterTimestamp,
 * updates recipient's lastReceivedLetterTimestamp
 * sends the letter to the recipient,
 */
@Singleton
class Mailman
@Inject constructor(private val letterDao: LetterDao,
                    private val userDao: UserDao,
                    private val recipientFinder: RecipientFinder,
                    private val notifier: Notifier) {

    private val log = logger()

    fun mail(letter: Letter) {
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
        if (!isReply) {
            updateRecipient(recipientId)
        }

        // update the letter with the sentTimestamp and recipient
        updateLetter(letter, recipientId, recipientAvatar)

        if (isReply) {
            notifier.receivedReply(letter.id, recipientId, recipientAvatar)
        }
        else {
            notifier.receivedLetter(letter.id, recipientId, recipientAvatar)
        }
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
}
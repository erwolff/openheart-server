package art.openhe.brains

import art.openhe.dao.LetterDao
import art.openhe.dao.UserDao
import art.openhe.model.Letter
import art.openhe.util.UpdateQuery
import art.openhe.util.logger
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

    private val log = logger()

    fun process(letter: Letter) {
        val recipient =
            if(letter.replyId != null) letter.recipientId to letter.recipientAvatar
            else findRecipient(letter)

        val recipientId = recipient?.first
        val recipientAvatar = recipient?.second

        if (recipient == null || recipientId == null || recipientAvatar == null) {
            log.error("Unable to find recipient for letter from author ${letter.authorId}")
            return
        }

        // send letter to recipient
        val sentTimestamp = sendLetter(letter, recipientId, recipientAvatar)

        // update the letter with the sentTimestamp and recipient
        updateLetter(letter, sentTimestamp, recipientId, recipientAvatar)
    }

    private fun findRecipient(letter: Letter): Pair<String, String>? {
        log.info("Finding recipient for letter from author ${letter.authorId}")

        return  recipientFinder.find(letter)?.let {
            it.id to it.avatar
        }
    }

    private fun sendLetter(letter: Letter, recipientId: String, recipientAvatar: String): Long {
        log.info("Sending letter from author ${letter.authorId} to recipient $recipientId")

        // send letter to recipientId
        //TODO
        // send to app (with recipientId): letter.toLetterResponse(recipientId, recipientAvatar)

        // update author's lastSentLetterTimestamp
        userDao.update(letter.authorId, UpdateQuery("lastSentLetterTimestamp" to DateTimeUtils.currentTimeMillis()))

        // update recipient's lastReceivedLetterTimestamp
        userDao.update(recipientId, UpdateQuery("lastReceivedLetterTimestamp" to DateTimeUtils.currentTimeMillis()))

        //return sentTimestamp
        return DateTimeUtils.currentTimeMillis()
    }

    private fun updateLetter(letter: Letter, sentTimestamp: Long, recipientId: String, recipientAvatar: String) {
        log.info("Updating letter from author ${letter.authorId}")

        letterDao.update(letter.id, UpdateQuery(
            "sentTimestamp" to sentTimestamp,
            "recipientId" to recipientId,
            "recipientAvatar" to recipientAvatar))
    }
}
package art.openhe.brains

import art.openhe.config.EnvConfig
import art.openhe.dao.LetterDao
import art.openhe.dao.UserDao
import art.openhe.dao.ext.count
import art.openhe.model.Letter
import art.openhe.model.ext.isReply
import art.openhe.model.ext.updateWith
import art.openhe.util.DbUpdate
import art.openhe.util.ext.eqCriteria
import art.openhe.util.logger
import com.google.common.annotations.VisibleForTesting
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
                    private val notifier: Notifier,
                    private val envConfig: EnvConfig) {

    private val log = logger()

    fun mail(letter: Letter) {
        if (letter.isReply()) processReply(letter)
        else processNewLetter(letter)

        if (isFirstLetterByAuthor(letter.authorId)) {
            log.info("Letter ${letter.id} is first letter written by author ${letter.authorId} - sending welcome letter")
            sendWelcomeLetter(letter.authorId, letter.authorAvatar)
        }
    }

    @VisibleForTesting
    fun findRecipient(letter: Letter): Pair<String, String>? {
        log.info("Finding recipient for letter from author ${letter.authorId}")

        return recipientFinder.find(letter)?.let {
            it.id to it.avatar
        }
    }

    @VisibleForTesting
    fun processReply(letter: Letter) {
        if (letter.recipientId == null || letter.recipientAvatar == null) {
            log.error("No recipientId or recipientAvatar on reply letter with parentId: ${letter.parentId} from author: ${letter.authorId}")
            return
        }

        // set the childId on the original letter
        updateOriginalLetter(letter.parentId!!, letter.id)

        log.info("Sending reply letter from author ${letter.authorId} to recipient ${letter.recipientId}")

        // update the reply letter with the sentTimestamp and recipient
        updateLetter(letter, letter.recipientId, letter.recipientAvatar)

        // notify the recipient of the reply
        notifier.receivedReply(letter.id, letter.recipientId, letter.recipientAvatar)
    }

    @VisibleForTesting
    fun processNewLetter(letter: Letter) {
        val recipient = findRecipient(letter)

        val recipientId = recipient?.first
        val recipientAvatar = recipient?.second

        if (recipient == null || recipientId == null || recipientAvatar == null) {
            log.error("Unable to find recipient for letter from author ${letter.authorId}")
            return
        }

        log.info("Sending new letter from author ${letter.authorId} to recipient $recipientId")

        // update the recipient's lastReceivedLetterTimestamp - but only if this is not a reply
        updateRecipient(recipientId)

        // update the letter with the sentTimestamp and recipient
        updateLetter(letter, recipientId, recipientAvatar)

        // notify the recipient of the letter
        notifier.receivedLetter(letter.id, recipientId, recipientAvatar)
    }

    @VisibleForTesting
    fun updateOriginalLetter(originalLetterId: String, childId: String) =
        letterDao.update(DbUpdate(originalLetterId, "childId" to childId))

    @VisibleForTesting
    fun updateRecipient(recipientId: String) =
        // update recipient's lastReceivedLetterTimestamp
        userDao.update(DbUpdate(recipientId, "lastReceivedLetterTimestamp" to DateTimeUtils.currentTimeMillis()))

    @VisibleForTesting
    fun updateLetter(letter: Letter, recipientId: String, recipientAvatar: String) =
        letterDao.update(letter.updateWith(
            "sentTimestamp" to DateTimeUtils.currentTimeMillis(),
            "recipientId" to recipientId,
            "recipientAvatar" to recipientAvatar))

    @VisibleForTesting
    fun isFirstLetterByAuthor(authorId: String) =
        letterDao.count(authorId = authorId.eqCriteria()) <= 1

    @VisibleForTesting
    fun sendWelcomeLetter(id: String, avatar: String) {
        // generate a welcome letter to this user
        generateAndStoreWelcomeLetterForUser(id, avatar)?.let {
            // send the welcome letter to this user
            notifier.welcomeLetter(id, avatar, it)

            // update this user's lastReceivedLetterTimestamp (because they're receiving the welcome letter)
            updateRecipient(id)
        }
    }

    @VisibleForTesting
    fun generateAndStoreWelcomeLetterForUser(id: String, avatar: String): String? {
        val welcomeLetterTemplate = letterDao.findById(envConfig.welcomeLetterId()) ?: return null
        return letterDao.save(Letter(
            authorId = welcomeLetterTemplate.authorId,
            authorAvatar = welcomeLetterTemplate.authorAvatar,
            recipientId = id,
            recipientAvatar = avatar,
            body = welcomeLetterTemplate.body,
            writtenTimestamp = DateTimeUtils.currentTimeMillis(),
            sentTimestamp = DateTimeUtils.currentTimeMillis()
        ))?.id
    }
}
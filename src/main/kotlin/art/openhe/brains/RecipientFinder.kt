package art.openhe.brains

import art.openhe.storage.dao.UserDao
import art.openhe.storage.dao.criteria.Sort
import art.openhe.storage.dao.ext.findOne
import art.openhe.model.Letter
import art.openhe.model.User
import art.openhe.util.ext.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Determines a valid recipient to receive this letter
 */
@Singleton
class RecipientFinder
@Inject constructor(private val userDao: UserDao) {

    //TODO: We'll want to do some more stuff here, but this is the basic implementation
    fun find(letter: Letter): User? =
        findUserWithOldestLastReceivedLetterTimestamp(letter)


    // Helper Functions

    private val findUserWithOldestLastReceivedLetterTimestamp = { letter: Letter ->
        userDao.findOne(
            sort = Sort.Users.byLastReceivedLetterTimestampAsc(),
            id = letter.authorId.asObjectId?.neCriteria(),
            lastSentLetterTimestamp = 0L.gtCriteria()
        )
    }
}
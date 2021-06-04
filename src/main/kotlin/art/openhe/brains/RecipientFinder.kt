package art.openhe.brains

import art.openhe.dao.UserDao
import art.openhe.dao.criteria.Sort
import art.openhe.dao.ext.findOne
import art.openhe.model.Letter
import art.openhe.model.User
import art.openhe.util.ext.*
import org.joda.time.DateTimeUtils
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecipientFinder
@Inject constructor(private val userDao: UserDao) {

    //TODO: We'll want to do some more stuff here, but this is the basic implementation
    fun find(letter: Letter): User? =
        userDao.findOne(
            sort = Sort.Users.byLastReceivedLetterTimestampAsc(),
            id = letter.authorId.asObjectId?.neCriteria(),
            lastSentLetterTimestamp = 0L.gtCriteria(),
            lastReceivedLetterTimestamp = (DateTimeUtils.currentTimeMillis().minus(TimeUnit.DAYS.toMillis(1))).lteCriteria()
        )
            ?: userDao.findOne(
                sort = Sort.Users.byLastReceivedLetterTimestampAsc(),
                id = letter.authorId.asObjectId?.neCriteria(),
                lastSentLetterTimestamp = 0L.gtCriteria()
            )
}
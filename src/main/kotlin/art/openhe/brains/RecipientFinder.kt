package art.openhe.brains

import art.openhe.dao.UserDao
import art.openhe.dao.criteria.Sort
import art.openhe.dao.criteria.ValueCriteria.Companion.gt
import art.openhe.dao.criteria.ValueCriteria.Companion.lte
import art.openhe.dao.criteria.ValueCriteria.Companion.ne
import art.openhe.dao.ext.findOne
import art.openhe.model.Letter
import art.openhe.model.User
import art.openhe.util.ext.*
import art.openhe.util.logger
import org.joda.time.DateTimeUtils
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecipientFinder
@Inject constructor(private val userDao: UserDao) {

    private val log = logger()

    //TODO: We'll want to do some more stuff here, but this is the basic implementation
    fun find(letter: Letter): User? =
        userDao.findOne(
            sort = Sort.Users.byLastReceivedLetterTimestampAsc(),
            id = ne(letter.authorId.asObjectId()),
            lastSentLetterTimestamp = gt(0L),
            lastReceivedLetterTimestamp = lte(DateTimeUtils.currentTimeMillis().minus(TimeUnit.DAYS.toMillis(1)))
        )
            ?: userDao.findOne(
                sort = Sort.Users.byLastReceivedLetterTimestampAsc(),
                id = ne(letter.authorId.asObjectId()),
                lastSentLetterTimestamp = gt(0L)
            )
}
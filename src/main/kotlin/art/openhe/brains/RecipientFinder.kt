package art.openhe.brains

import art.openhe.dao.UserDao
import art.openhe.model.Message
import art.openhe.model.User
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
    fun find(message: Message): User? =
        userDao.findOneByLastReceivedMessageTimestampLessThan(message.authorId,
            DateTimeUtils.currentTimeMillis().minus(TimeUnit.DAYS.toMillis(1)))
}
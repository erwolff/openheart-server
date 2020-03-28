package art.openhe.dao.ext

import art.openhe.dao.UserDao
import art.openhe.model.Page
import art.openhe.model.User
import art.openhe.util.MongoQuery.Users.email
import art.openhe.util.MongoQuery.Users.googleId
import art.openhe.util.MongoQuery.Users.hearts
import art.openhe.util.MongoQuery.Users.lastReceivedLetterTimestamp
import art.openhe.util.MongoQuery.Users.lastSentLetterTimestamp
import art.openhe.util.MongoQuery.and
import art.openhe.util.MongoQuery.id
import art.openhe.util.MongoQuery.lt
import art.openhe.util.MongoQuery.ne
import art.openhe.util.ext.letAsObjectId
import art.openhe.util.ext.runQuery
import kotlin.math.ceil


fun UserDao.findOneByLastReceivedLetterTimestampLessThan(excludeId: String, timestamp: Long): User? =
    excludeId.letAsObjectId { oid ->
        collection.runQuery { it.findOne(
            and(
                id(ne(excludeId)),
                lastReceivedLetterTimestamp(lt(timestamp)))
        ).`as`(User::class.java) }
    }


fun UserDao.findOne(
    googleId: String? = null,
    email: String? = null,
    lastReceivedLetterTimestamp: Any? = null,
    lastSentLetterTimestamp: Any? = null,
    hearts: Any? = null
): User? =
    collection.runQuery { it.findOne(
        andQuery(googleId, email, lastReceivedLetterTimestamp, lastSentLetterTimestamp, hearts)).`as`(User::class.java)
    }


fun UserDao.find(
    page: Int,
    size: Int,
    sort: String,
    googleId: String? = null,
    email: String? = null,
    lastReceivedLetterTimestamp: Any? = null,
    lastSentLetterTimestamp: Any? = null,
    hearts: Any? = null
): Page<User>? {
    val query = andQuery(googleId, email, lastReceivedLetterTimestamp, lastSentLetterTimestamp, hearts)

    val totalResults = collection.runQuery { it.count(query) } ?: 0

    val results = collection.runQuery {
        it.find(query)
            .sort(sort)
            .skip(if (page == 0) 0 else page.minus(1) * size)
            .limit(size)
            .`as`(User::class.java)
    }

    return results?.let {
        Page(it.toList(), page, size, totalResults, ceil(totalResults / size.toDouble()).toInt())
    }
}


private fun andQuery(
    googleId: String? = null,
    email: String? = null,
    lastReceivedLetterTimestamp: Any? = null,
    lastSentLetterTimestamp: Any? = null,
    hearts: Any? = null
): String {
    val criteria = mutableListOf<String>().apply {
        googleId?.let { add(googleId(it)) }
        email?.let { add(email(it)) }
        lastReceivedLetterTimestamp?.let { add(lastReceivedLetterTimestamp(lastReceivedLetterTimestamp)) }
        lastSentLetterTimestamp?.let { add(lastSentLetterTimestamp(lastSentLetterTimestamp)) }
        hearts?.let { add(hearts(hearts)) }
    }.toTypedArray()

    return when {
        criteria.isEmpty() -> ""
        criteria.size == 1 -> criteria[0]
        else -> and(*criteria)
    }
}
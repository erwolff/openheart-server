package art.openhe.dao.ext

import art.openhe.dao.UserDao
import art.openhe.dao.criteria.IdCriteria
import art.openhe.dao.criteria.NumberCriteria
import art.openhe.dao.criteria.Sort
import art.openhe.dao.criteria.StringCriteria
import art.openhe.model.Page
import art.openhe.model.User
import art.openhe.util.ext.runQuery
import kotlin.math.ceil


fun UserDao.findOne(
    sort: Sort? = null,
    id: IdCriteria? = null,
    googleId: StringCriteria? = null,
    email: StringCriteria? = null,
    lastReceivedLetterTimestamp: NumberCriteria? = null,
    lastSentLetterTimestamp: NumberCriteria? = null,
    hearts: NumberCriteria? = null
): User? {
    val query = andQuery(id, googleId, email, lastReceivedLetterTimestamp, lastSentLetterTimestamp, hearts)

    return sort?.let { s ->
        collection.runQuery {
            it.find(query)
                .sort(s.toSort())
                .limit(1)
                .`as`(User::class.java).firstOrNull()
        }
    }
        ?: collection.runQuery {
            it.findOne(query).`as`(User::class.java)
        }
}


fun UserDao.find(
    page: Int,
    size: Int,
    sort: Sort,
    id: IdCriteria? = null,
    googleId: StringCriteria? = null,
    email: StringCriteria? = null,
    lastReceivedLetterTimestamp: NumberCriteria? = null,
    lastSentLetterTimestamp: NumberCriteria? = null,
    hearts: NumberCriteria? = null
): Page<User>? {
    val query = andQuery(id, googleId, email, lastReceivedLetterTimestamp, lastSentLetterTimestamp, hearts)

    val totalResults = collection.runQuery { it.count(query) } ?: 0

    val results = collection.runQuery {
        it.find(query)
            .sort(sort.toSort())
            .skip(if (page == 0) 0 else page.minus(1) * size)
            .limit(size)
            .`as`(User::class.java)
    }

    return results?.let {
        Page(it.toList(), page, size, totalResults, ceil(totalResults / size.toDouble()).toInt())
    }
}


private fun andQuery(
    id: IdCriteria? = null,
    googleId: StringCriteria? = null,
    email: StringCriteria? = null,
    lastReceivedLetterTimestamp: NumberCriteria? = null,
    lastSentLetterTimestamp: NumberCriteria? = null,
    hearts: NumberCriteria? = null
): String {
    val criteria = mutableListOf<String>().apply {
        id?.let { add(it.toCriteria()) }
        googleId?.let { add(it.toCriteria("googleId")) }
        email?.let { add(it.toCriteria("email")) }
        lastReceivedLetterTimestamp?.let { addAll(it.toCriteria("lastReceivedLetterTimestamp")) }
        lastSentLetterTimestamp?.let { addAll(it.toCriteria("lastSentLetterTimestamp")) }
        hearts?.let { addAll(it.toCriteria("hearts")) }
    }.toTypedArray()

    return when {
        criteria.isEmpty() -> ""
        criteria.size == 1 -> criteria[0]
        else -> "{ \$and: [ " + criteria.joinToString { it } + " ] }"
    }
}
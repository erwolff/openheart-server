package art.openhe.storage.dao.ext

import art.openhe.storage.dao.UserDao
import art.openhe.storage.dao.criteria.*
import art.openhe.model.Page
import art.openhe.model.User
import org.bson.types.ObjectId


fun UserDao.findOne(
    sort: Sort? = null,
    id: ValueCriteria<ObjectId>? = null,
    googleId: ValueCriteria<String>? = null,
    email: ValueCriteria<String>? = null,
    lastReceivedLetterTimestamp: ValueCriteria<Long>? = null,
    lastSentLetterTimestamp: ValueCriteria<Long>? = null,
    hearts: ValueCriteria<Long>? = null
): User? =
    findOne(
        andQuery(id, googleId, email, lastReceivedLetterTimestamp, lastSentLetterTimestamp, hearts),
        sort,
        User::class.java
    )


fun UserDao.find(
    pageable: Pageable,
    id: ValueCriteria<ObjectId>? = null,
    googleId: ValueCriteria<String>? = null,
    email: ValueCriteria<String>? = null,
    lastReceivedLetterTimestamp: ValueCriteria<Long>? = null,
    lastSentLetterTimestamp: ValueCriteria<Long>? = null,
    hearts: ValueCriteria<Long>? = null
): Page<User> =
    find(
        andQuery(id, googleId, email, lastReceivedLetterTimestamp, lastSentLetterTimestamp, hearts),
        pageable,
        User::class.java
    )


private fun andQuery(
    id: ValueCriteria<ObjectId>? = null,
    googleId: ValueCriteria<String>? = null,
    email: ValueCriteria<String>? = null,
    lastReceivedLetterTimestamp: ValueCriteria<Long>? = null,
    lastSentLetterTimestamp: ValueCriteria<Long>? = null,
    hearts: ValueCriteria<Long>? = null
) = andQuery(
    "_id" to id,
    "googleId" to googleId,
    "email" to email,
    "lastReceivedLetterTimestamp" to lastReceivedLetterTimestamp,
    "lastSentLetterTimestamp" to lastSentLetterTimestamp,
    "hearts" to hearts)

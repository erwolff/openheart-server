package art.openhe.dao.ext

import art.openhe.dao.LetterDao
import art.openhe.dao.criteria.*
import art.openhe.model.Letter
import art.openhe.model.Page
import art.openhe.util.ext.runQuery
import org.bson.types.ObjectId
import kotlin.math.ceil


fun LetterDao.findOne(
    sort: Sort? = null,
    id: ValueCriteria<ObjectId>? = null,
    authorId: ValueCriteria<String>? = null,
    recipientId: ValueCriteria<String>? = null,
    parentId: ValueCriteria<String>? = null,
    childId: ValueCriteria<String>? = null,
    hearted: ValueCriteria<Boolean>? = null,
    deleted: ValueCriteria<Boolean>? = null
): Letter? =
    findOne(
        andQuery(id, authorId, recipientId, parentId, childId, hearted, deleted),
        sort,
        Letter::class.java
    )



fun LetterDao.find(
    page: Int,
    size: Int,
    sort: Sort,
    id: ValueCriteria<ObjectId>? = null,
    authorId: ValueCriteria<String>? = null,
    recipientId: ValueCriteria<String>? = null,
    parentId: ValueCriteria<String>? = null,
    childId: ValueCriteria<String>? = null,
    hearted: ValueCriteria<Boolean>? = null,
    deleted: ValueCriteria<Boolean>? = null
): Page<Letter> =
    find(
        andQuery(id, authorId, recipientId, parentId, childId, hearted, deleted),
        page, size, sort,
        Letter::class.java
    )


fun LetterDao.count(
    authorId: ValueCriteria<String>? = null,
    recipientId: ValueCriteria<String>? = null
): Long =
    count(
        andQuery(authorId = authorId, recipientId = recipientId)
    )



private fun andQuery(
    id: ValueCriteria<ObjectId>? = null,
    authorId: ValueCriteria<String>? = null,
    recipientId: ValueCriteria<String>? = null,
    parentId: ValueCriteria<String>? = null,
    childId: ValueCriteria<String>? = null,
    hearted: ValueCriteria<Boolean>? = null,
    deleted: ValueCriteria<Boolean>? = null
) = andQuery(
        "_id" to id,
        "authorId" to authorId,
        "recipientId" to recipientId,
        "parentId" to parentId,
        "childId" to childId,
        "hearted" to hearted,
        "deleted" to deleted)



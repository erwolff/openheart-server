package art.openhe.dao.ext

import art.openhe.dao.LetterDao
import art.openhe.model.Letter
import art.openhe.model.Page
import art.openhe.util.MongoQuery.Letters.authorId
import art.openhe.util.MongoQuery.Letters.childId
import art.openhe.util.MongoQuery.Letters.hearted
import art.openhe.util.MongoQuery.Letters.parentId
import art.openhe.util.MongoQuery.Letters.recipientId
import art.openhe.util.MongoQuery.and
import art.openhe.util.MongoQuery.ne
import art.openhe.util.ext.runQuery
import kotlin.math.ceil


fun LetterDao.findOne(
    authorId: String? = null,
    recipientId: String? = null,
    parentId: String? = null,
    childId: String? = null,
    hearted: Boolean? = null,
    reply: Boolean? = null
): Letter? =
    collection.runQuery { it.findOne(
        andQuery(authorId, recipientId, parentId, childId, hearted, reply)).`as`(Letter::class.java)
    }


fun LetterDao.find(
    page: Int,
    size: Int,
    sort: String,
    authorId: String? = null,
    recipientId: String? = null,
    parentId: String? = null,
    childId: String? = null,
    hearted: Boolean? = null,
    reply: Boolean? = null
): Page<Letter>? {

    val query = andQuery(authorId, recipientId, parentId, childId, hearted, reply)

    val totalResults = collection.runQuery { it.count(query) } ?: 0

    val results = collection.runQuery {
        it.find(query)
            .sort(sort)
            .skip(if (page == 0) 0 else page.minus(1) * size)
            .limit(size)
            .`as`(Letter::class.java)
    }

    return results?.let {
        Page(it.toList(), page, size, totalResults, ceil(totalResults / size.toDouble()).toInt())
    }
}


private fun andQuery(
    authorId: String? = null,
    recipientId: String? = null,
    parentId: String? = null,
    childId: String? = null,
    hearted: Boolean? = null,
    reply: Boolean? = null
): String {
    val criteria = mutableListOf<String>().apply {
        authorId?.let { add(authorId(it)) }
        recipientId?.let { add(recipientId(it)) }
        parentId?.let { add(parentId(it)) }
        childId?.let { add(childId(it)) }
        hearted?.let { add(hearted(it)) }
        reply?.let { add(parentId(if (reply) ne(null) else null)) }
    }.toTypedArray()

    return when {
        criteria.isEmpty() -> ""
        criteria.size == 1 -> criteria[0]
        else -> and(*criteria)
    }
}

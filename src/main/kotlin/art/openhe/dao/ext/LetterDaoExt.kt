package art.openhe.dao.ext

import art.openhe.dao.LetterDao
import art.openhe.dao.criteria.BoolCriteria
import art.openhe.dao.criteria.IdCriteria
import art.openhe.dao.criteria.Sort
import art.openhe.dao.criteria.StringCriteria
import art.openhe.model.Letter
import art.openhe.model.Page
import art.openhe.util.ext.runQuery
import kotlin.math.ceil


fun LetterDao.findOne(
    sort: Sort? = null,
    id: IdCriteria? = null,
    authorId: StringCriteria? = null,
    recipientId: StringCriteria? = null,
    parentId: StringCriteria? = null,
    childId: StringCriteria? = null,
    hearted: BoolCriteria? = null,
    deleted: BoolCriteria? = null
): Letter? {
    val query = andQuery(id, authorId, recipientId, parentId, childId, hearted, deleted)

    return sort?.let { s ->
        collection.runQuery {
            it.find(query)
                .sort(s.toSort())
                .limit(1)
                .`as`(Letter::class.java).firstOrNull()
        }
    }
        ?: collection.runQuery {
            it.findOne(query).`as`(Letter::class.java)
        }
}


fun LetterDao.find(
    page: Int,
    size: Int,
    sort: Sort,
    id: IdCriteria? = null,
    authorId: StringCriteria? = null,
    recipientId: StringCriteria? = null,
    parentId: StringCriteria? = null,
    childId: StringCriteria? = null,
    hearted: BoolCriteria? = null,
    deleted: BoolCriteria? = null
): Page<Letter>? {

    val query = andQuery(id, authorId, recipientId, parentId, childId, hearted, deleted)

    val totalResults = collection.runQuery { it.count(query) } ?: 0

    val results = collection.runQuery {
        it.find(query)
            .sort(sort.toSort())
            .skip(if (page == 0) 0 else page.minus(1) * size)
            .limit(size)
            .`as`(Letter::class.java)
    }

    return results?.let {
        Page(it.toList(), page, size, totalResults, ceil(totalResults / size.toDouble()).toInt())
    }
}


private fun andQuery(
    id: IdCriteria? = null,
    authorId: StringCriteria? = null,
    recipientId: StringCriteria? = null,
    parentId: StringCriteria? = null,
    childId: StringCriteria? = null,
    hearted: BoolCriteria? = null,
    deleted: BoolCriteria? = null
): String {
    val criteria = mutableListOf<String>().apply {
        id?.let { add(it.toCriteria()) }
        authorId?.let { add(it.toCriteria("authorId")) }
        recipientId?.let { add(it.toCriteria("recipientId")) }
        parentId?.let { add(it.toCriteria("parentId")) }
        childId?.let { add(it.toCriteria("childId")) }
        hearted?.let { add(it.toCriteria("hearted")) }
        deleted?.let { add(it.toCriteria("deleted")) }
    }.toTypedArray()

    return when {
        criteria.isEmpty() -> ""
        criteria.size == 1 -> criteria[0]
        else -> "{ \$and: [ " + criteria.joinToString { it } + " ] }"
    }
}

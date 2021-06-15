package art.openhe.storage.dao.ext

import art.openhe.storage.dao.Dao
import art.openhe.storage.dao.criteria.Pageable
import art.openhe.storage.dao.criteria.Sort
import art.openhe.storage.dao.criteria.ValueCriteria
import art.openhe.model.DbObject
import art.openhe.model.Page
import art.openhe.util.ext.runQuery
import kotlin.math.ceil

fun Dao.count(query: String): Long = collection.runQuery { it.count(query) } ?: 0

fun <T> Dao.findOne(query: String, sort: Sort?, clazz: Class<T>): T? =
    if (sort != null)
        collection.runQuery {
            it.find(query)
                .sort(sort.toSort())
                .limit(1)
                .`as`(clazz)
                .firstOrNull() }
    else
        collection.runQuery { it.findOne(query).`as`(clazz) }


fun <T: DbObject> Dao.find(query: String, pageable: Pageable, clazz: Class<T>): Page<T> {
    val totalResults = count(query)

    val results = collection.runQuery {
        it.find(query)
            .sort(pageable.sort.toSort())
            .skip(pageable.skip)
            .limit(pageable.size)
            .`as`(clazz)
    }

    return results?.let {
        Page(it.toList(), pageable.page, pageable.size, totalResults, ceil(totalResults / pageable.size.toDouble()).toInt())
    } ?: noResults()
}

fun andQuery(vararg criteria: Pair<String, ValueCriteria<*>?>) =
    mutableListOf<String>().apply{
        criteria.mapNotNull { it.second?.toCriteria(it.first) }.toTypedArray()
    }.toAndQueryString()


private fun List<String>.toAndQueryString() = when {
    isEmpty() -> ""
    size == 1 -> this[0]
    else -> "{ \$and: [ " + this.joinToString { it } + " ] }"
}

 fun <T: DbObject> noResults(): Page<T> = Page(listOf(), 0, 0, 0, 0)
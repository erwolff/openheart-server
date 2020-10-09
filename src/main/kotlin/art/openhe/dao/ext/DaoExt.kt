package art.openhe.dao.ext

import art.openhe.dao.Dao
import art.openhe.dao.criteria.Sort
import art.openhe.dao.criteria.ValueCriteria
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


fun <T> Dao.find(query: String, page: Int, size: Int, sort: Sort, clazz: Class<T>): Page<T> {
    val totalResults = count(query)

    val results = collection.runQuery {
        it.find(query)
            .sort(sort.toSort())
            .skip(if (page == 0) 0 else page.minus(1) * size)
            .limit(size)
            .`as`(clazz)
    }

    return results?.let {
        Page(it.toList(), page, size, totalResults, ceil(totalResults / size.toDouble()).toInt())
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

 fun <T> noResults(): Page<T> = Page(listOf(), 0, 0, 0, 0)
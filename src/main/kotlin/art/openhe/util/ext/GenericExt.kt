package art.openhe.util.ext

import art.openhe.storage.dao.criteria.ValueCriteria
import org.bson.types.ObjectId

/**
 * Returns a ValueCriteria<T> of type Eq
 */
fun <T: Any> T.eqCriteria(): ValueCriteria<T> = ValueCriteria.eq(this)

/**
 * Returns a ValueCriteria<T> of type Ne
 */
fun <T: Any> T.neCriteria(): ValueCriteria<T> = ValueCriteria.ne(this)

/**
 * Returns a ValueCriteria<T> of type Lt
 */
fun <T: Number> T.ltCriteria(): ValueCriteria<T> = ValueCriteria.lt(this)

/**
 * Returns a ValueCriteria<T> of type Lte
 */
fun <T: Number> T.lteCriteria(): ValueCriteria<T> = ValueCriteria.lte(this)

/**
 * Returns a ValueCriteria<T> of type Gt
 */
fun <T: Number> T.gtCriteria(): ValueCriteria<T> = ValueCriteria.gt(this)

/**
 * Returns a ValueCriteria<T> of type Gte
 */
fun <T: Number> T.gteCriteria(): ValueCriteria<T> = ValueCriteria.gte(this)

fun <T: Any> T.toClause(): String =
    when(this) {
        is String -> "\"${this}\""
        is Number -> "$this"
        is Boolean -> "$this"
        is ObjectId -> "{ \$oid: \"${this.toHexString()}\" }"
        else -> throw UnsupportedOperationException("Unsupported toQuery call with type: ${this::class.java.simpleName}")
    }

fun <T: Any> Collection<T>.toClause(): String =
    "[ ${this.joinToString(",") { it.toClause() }} ]"

package art.openhe.util.ext

import art.openhe.dao.criteria.ValueCriteria

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

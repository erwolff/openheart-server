package art.openhe.storage.dao.criteria

import art.openhe.util.ext.toClause
import org.apache.commons.collections4.CollectionUtils


sealed class ValueCriteria<T: Any> {

    companion object {
        fun <T: Any> eq(value: T): ValueCriteria<T> = Eq(value)
        fun <T: Any> ne(value: T): ValueCriteria<T> = Ne(value)
        fun <T: Any> isIn(vararg values: T): ValueCriteria<T> = if (values.size == 1) Eq(values.first()) else IsIn(setOf(*values))
        fun <T: Any> isIn(values: Collection<T>): ValueCriteria<T> = if (values.size == 1) Eq(values.first()) else IsIn(values)
        fun <T: Any> isNotIn(vararg values: T): ValueCriteria<T> = if (values.size == 1) Ne(values.first()) else IsNotIn(setOf(*values))
        fun <T: Any> isNotIn(values: Collection<T>): ValueCriteria<T> = if (values.size == 1) Ne(values.first()) else IsNotIn(values)
        fun <T: Number> lt(value: T): ValueCriteria<T> = Lt(value)
        fun <T: Number> lte(value: T): ValueCriteria<T> = Lte(value)
        fun <T: Number> gt(value: T): ValueCriteria<T> = Gt(value)
        fun <T: Number> gte(value: T): ValueCriteria<T> = Gte(value)
        fun <T: Number> between(min: T, max: T): ValueCriteria<T> = Between(min, max)
        fun <T: Any> isNotNull(): ValueCriteria<T> = IsNotNull()
        fun <T: Any> isNull(): ValueCriteria<T> = IsNull()
        fun isTrue(): ValueCriteria<Boolean> = Eq(true)
        fun isFalse(): ValueCriteria<Boolean> = Eq(false)
    }


    private data class Eq<T: Any>(val value: T): ValueCriteria<T>()
    private data class Ne<T: Any>(val value: T): ValueCriteria<T>()
    private data class IsIn<T: Any>(val values: Collection<T>): ValueCriteria<T>()
    private data class IsNotIn<T: Any>(val values: Collection<T>): ValueCriteria<T>()
    private data class Lt<T: Number>(val value: T): ValueCriteria<T>()
    private data class Lte<T: Number>(val value: T): ValueCriteria<T>()
    private data class Gt<T: Number>(val value: T): ValueCriteria<T>()
    private data class Gte<T: Number>(val value: T): ValueCriteria<T>()
    private data class Between<T: Number>(val min: T, val max: T): ValueCriteria<T>()
    private class IsNotNull<T: Any>: ValueCriteria<T>()
    private class IsNull<T: Any>: ValueCriteria<T>()

    fun toCriteria(field: String): String = when(this) {
        is Between -> "{ \$and: [ $field: { \$gt: ${this.min} } }, { $field: { \$lt: ${this.max} } } ]"
        else -> this.getQueryString(field).replace("#", this.getValues().firstOrNull()?.toClause() ?: "null")
    }

    private fun getQueryString(field: String): String =
        "{ $field: { ${this.getOp()}: # } }"

    private fun getOp(): String = when(this) {
        is Eq, is IsNull -> "\$eq"
        is Ne, is IsNotNull -> "\$ne"
        is IsIn -> "\$in"
        is IsNotIn -> "\$nin"
        is Lt -> "\$lt"
        is Lte -> "\$lte"
        is Gt -> "\$gt"
        is Gte -> "\$gte"
        is Between -> throw UnsupportedOperationException("Unsupported getOp call using Between criteria")
    }

    fun getValues(): List<T> = when(this)  {
        is Eq -> listOf(this.value)
        is Ne -> listOf(this.value)
        is IsIn -> this.values.toList()
        is IsNotIn -> this.values.toList()
        is Lt -> listOf(this.value)
        is Lte -> listOf(this.value)
        is Gt -> listOf(this.value)
        is Gte -> listOf(this.value)
        is Between -> listOf(this.min, this.max)
        is IsNotNull -> emptyList()
        is IsNull -> emptyList()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return when (this) {
            is Eq -> this.value == (other as Eq<*>).value
            is Ne -> this.value == (other as Ne<*>).value
            is IsIn -> this.values.isCollectionEqualTo((other as IsIn<*>).values)
            is IsNotIn -> this.values.isCollectionEqualTo((other as IsNotIn<*>).values)
            is Lt -> this.value == (other as Lt<*>).value
            is Lte -> this.value == (other as Lte<*>).value
            is Gt -> this.value == (other as Gt<*>).value
            is Gte -> this.value == (other as Gte<*>).value
            is Between -> this.min == (other as Between<*>).min && this.max == other.max
            is IsNotNull -> true
            is IsNull -> true
        }
    }

    private fun Collection<*>.isCollectionEqualTo(other: Collection<*>) =
        (CollectionUtils.isEmpty(this) && CollectionUtils.isEmpty(other)) 
                || (CollectionUtils.isNotEmpty(this) 
                        && CollectionUtils.isNotEmpty(other) 
                        && CollectionUtils.isEqualCollection(this, other))

    override fun hashCode(): Int {
        return javaClass.hashCode()
    }
    
}
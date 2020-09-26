package art.openhe.dao.criteria

import org.apache.commons.collections4.CollectionUtils
import org.bson.types.ObjectId


sealed class ValueCriteria<T: Any> {

    companion object {
        fun <T: Any> eq(value: T): ValueCriteria<T> = Eq(value)
        fun <T: Any> ne(value: T): ValueCriteria<T> = Ne(value)
        fun <T: Any> isIn(vararg values: T): ValueCriteria<T> = IsIn(setOf(*values))
        fun <T: Any> isIn(values: Collection<T>): ValueCriteria<T> = IsIn(values)
        fun <T: Any> isNotIn(vararg values: T): ValueCriteria<T> = IsNotIn(setOf(*values))
        fun <T: Any> isNotIn(values: Collection<T>): ValueCriteria<T> = IsNotIn(values)
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
        is Eq ->
            when(this.value) {
                is String -> "{ $field: \"${this.value}\" }"
                is Number -> "{ $field: ${this.value} }"
                is Boolean -> "{ $field: ${this.value} }"
                is ObjectId -> "{ _id: { \$oid: \"${this.value.toHexString()}\" } }"
                else -> throw UnsupportedOperationException("Unsupported Eq call with type: ${this::class.java.simpleName}")
            }
        is Ne ->
            when(this.value) {
                is String -> "{ $field: { \$ne: \"${this.value}\" } }"
                is Number -> "{ $field: { \$ne: ${this.value} } }"
                is ObjectId -> "{ _id: { \$ne: { \$oid: \"${this.value}\" } } }"
                else -> throw UnsupportedOperationException("Unsupported Ne call with type: ${this::class.java.simpleName}")
            }
        is IsIn ->
            if (this.values.size == 1) eq(this.values.first()).toCriteria(field)
            else when(this.values.first()) {
                is String -> "{ $field: { \$in: [\"${this.values.joinToString("\",\"")}\"] } }"
                is Number -> "{ $field: { \$in: [${this.values.joinToString(",")}] } }"
                else -> throw UnsupportedOperationException("Unsupported IsIn call with type: ${this::class.java.simpleName}")
            }
        is IsNotIn ->
            if (this.values.size == 1) ne(this.values.first()).toCriteria(field)
            else when(this.values.first()) {
                is String -> "{ $field: { \$nin: [\"${this.values.joinToString("\",\"")}\"] } }"
                is Number -> "{ $field: { \$nin: [${this.values.joinToString(",")}] } }"
                else -> throw UnsupportedOperationException("Unsupported IsNotIn call with type: ${this::class.java.simpleName}")
            }
        is Lt -> "{ $field: { \$lt: ${this.value} } }"
        is Lte -> "{ $field: { \$lte: ${this.value} } }"
        is Gt -> "{ $field: { \$gt: ${this.value} } }"
        is Gte -> "{ $field: { \$gte: ${this.value} } }"
        is Between ->
            "{ \$and: [ $field: { \$gte: ${this.min} } }, { $field: { \$lte: ${this.max} } } ]"
        is IsNotNull -> "{ $field: { \$ne: null } }"
        is IsNull -> "{ $field: { \$eq: null } }"
    }

    fun toString(field: String): String = when(this) {
        is Eq -> " $field eq ${this.value}"
        is Ne -> " $field ne ${this.value}"
        is IsIn -> " $field isIn ${this.values.joinToString(",")}"
        is IsNotIn -> " $field isNotIn ${this.values.joinToString(",")}"
        is Lt -> " $field lt ${this.value}"
        is Lte -> " $field lte ${this.value}"
        is Gt -> " $field gt ${this.value}"
        is Gte -> " $field gte ${this.value}"
        is Between -> " $field gte ${this.min} and lte ${this.max}"
        is IsNotNull -> " $field is not null"
        is IsNull -> " $field is null"
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
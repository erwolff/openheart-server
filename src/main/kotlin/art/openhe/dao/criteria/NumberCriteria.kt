package art.openhe.dao.criteria


sealed class NumberCriteria {

    companion object {
        fun lt(value: Number): NumberCriteria = Lt(value)
        fun lte(value: Number): NumberCriteria = Lte(value)
        fun gt(value: Number): NumberCriteria = Gt(value)
        fun gte(value: Number): NumberCriteria = Gte(value)
        fun between(min: Number, max: Number): NumberCriteria = Between(min, max)
        fun eq(value: Number): NumberCriteria = Eq(value)
        fun ne(value: Number): NumberCriteria = Ne(value)
        fun isIn(vararg values: Number): NumberCriteria = In(setOf(*values))
        fun isNull(): NumberCriteria = IsNull
        fun isNotNull(): NumberCriteria = IsNotNull
    }

    private data class Lt(val value: Number): NumberCriteria()
    private data class Lte(val value: Number): NumberCriteria()
    private data class Gt(val value: Number): NumberCriteria()
    private data class Gte(val value: Number): NumberCriteria()
    private data class Between(val min: Number, val max: Number): NumberCriteria()
    private data class Eq(val value: Number): NumberCriteria()
    private data class Ne(val value: Number): NumberCriteria()
    private data class In(val values: Collection<Number>): NumberCriteria()
    private object IsNull: NumberCriteria()
    private object IsNotNull: NumberCriteria()

    fun toCriteria(field: String): List<String> = when (this) {
        is Lt -> listOf("{ $field: { \$lt: ${this.value} } }")
        is Lte -> listOf("{ $field: { \$lte: ${this.value} } }")
        is Gt -> listOf("{ $field: { \$gt: ${this.value} } }")
        is Gte -> listOf("{ $field: { \$gte: ${this.value} } }")
        is Between -> listOf(
            "{ $field: { \$gte: ${this.min} } }",
            "{ $field: { \$lte: ${this.max} } }"
        )
        is Eq -> listOf("{ $field: ${this.value} }")
        is Ne -> listOf("{ $field: { \$ne: ${this.value} } }")
        is In -> listOf(
            if (this.values.size == 1) "{ $field: ${this.values.first()} }"
            else "{ $field: { \$in: [${this.values.joinToString(",")}] } }"
        )
        is IsNull -> listOf("{ $field: null }")
        is IsNotNull -> listOf("{ $field: { \$ne: null } }")
    }
}
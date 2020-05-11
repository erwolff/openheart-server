package art.openhe.dao.criteria


sealed class StringCriteria {

    companion object {
        fun eq(value: String): StringCriteria = Eq(value)
        fun ne(value: String): StringCriteria = Ne(value)
        fun isIn(vararg values: String): StringCriteria = In(setOf(*values))
        fun isNull(): StringCriteria = IsNull
        fun isNotNull(): StringCriteria = IsNotNull
    }

    private data class Eq(val value: String): StringCriteria()
    private data class Ne(val value: String): StringCriteria()
    private data class In(val values: Collection<String>): StringCriteria()
    private object IsNull: StringCriteria()
    private object IsNotNull: StringCriteria()

    fun toCriteria(field: String): String = when(this) {
        is Eq -> "{ $field: \"${this.value}\" }"
        is Ne -> "{ $field: { \$ne: \"${this.value}\" } }"
        is In ->
            if (this.values.size == 1) "{ $field: \"${this.values.first()}\" }"
            else "{ $field: { \$in: [\"${this.values.joinToString("\",\"")}\"] } }"

        is IsNull -> "{ $field: null }"
        is IsNotNull -> "{ $field: { \$ne: null } }"
    }
}
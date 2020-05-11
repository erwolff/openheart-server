package art.openhe.dao.criteria


sealed class IdCriteria {

    companion object {
        fun eq(value: String): IdCriteria = Eq(value)
        fun ne(value: String): IdCriteria = Ne(value)
    }

    private data class Eq(val value: String): IdCriteria()
    private data class Ne(val value: String): IdCriteria()

    fun toCriteria(): String = when(this) {
        is Eq -> "{ _id: { \$oid: \"${this.value}\" } }"
        is Ne -> "{ _id: { \$ne: { \$oid: \"${this.value}\" } } }"
    }
}
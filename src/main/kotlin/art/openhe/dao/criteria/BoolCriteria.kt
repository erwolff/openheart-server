package art.openhe.dao.criteria


sealed class BoolCriteria {

    companion object {
        fun eq(value: Boolean?) = value?.let { if (value) IsTrue else IsFalse }
        fun isTrue(): BoolCriteria = IsTrue
        fun isFalse(): BoolCriteria = IsFalse
    }

    object IsTrue: BoolCriteria()
    object IsFalse: BoolCriteria()

    fun toCriteria(field: String): String = when(this) {
        is IsTrue -> "{ $field: true }"
        is IsFalse -> "{ $field: false }"
    }
}
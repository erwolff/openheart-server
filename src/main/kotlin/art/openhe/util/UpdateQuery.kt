package art.openhe.util


class UpdateQuery
constructor(private vararg val fieldAndValues: Pair<String, Any>?) {

    private val updatePrefix = "{ \$set: { "
    private val updatePostfix = " } }"

    fun toQuery() =
        fieldAndValues.joinToString(prefix = updatePrefix, postfix = updatePostfix) {
            it?.let {
                "${it.first}:" + parseValue(it.second)
            } ?: updatePrefix + updatePostfix
        }

    private fun parseValue(value: Any?): String {
        return if (value is String) "\"$value\""
        else "$value"
    }
}
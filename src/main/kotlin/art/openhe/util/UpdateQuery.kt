package art.openhe.util


class UpdateQuery
constructor(private vararg val fieldAndValues: Pair<String, Any>?) {

    fun toQuery() =
        fieldAndValues.joinToString(prefix = MongoQuery.updatePrefix, postfix = MongoQuery.updatePostfix) {
            it?.let {
                "${it.first}:" + parseValue(it.second)
            } ?: MongoQuery.updatePrefix + MongoQuery.updatePostfix
        }

    private fun parseValue(value: Any?): String {
        return if (value is String) "\"$value\""
        else "$value"
    }
}
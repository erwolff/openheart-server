package art.openhe.util


class UpdateQuery
constructor(private vararg val fieldAndValues: Pair<String, Any>?) {

    fun toQuery() =
        fieldAndValues.joinToString(prefix = MongoQueryDefs.updatePrefix, postfix = MongoQueryDefs.updatePostfix) {
            it?.let {
                "${it.first}:" + parseValue(it.second)
            } ?: MongoQueryDefs.updatePrefix + MongoQueryDefs.updatePostfix
        }

    private fun parseValue(value: Any?): String {
        return if (value is String) "\"$value\""
        else "$value"
    }
}
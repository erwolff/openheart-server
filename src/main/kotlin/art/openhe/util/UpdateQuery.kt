package art.openhe.util


class UpdateQuery constructor(private vararg val fieldAndValues: Pair<String, Any>?) {



    fun toQuery() =
        fieldAndValues.joinToString(prefix = MongoUtil.updatePrefix, postfix = MongoUtil.updatePostfix) {
            it?.let {
                "${it.first}:" + parseValue(it.second)
            } ?: MongoUtil.updatePrefix + MongoUtil.updatePostfix
        }

    private fun parseValue(value: Any?): String {
        return if (value is String) "\"$value\""
        else "$value"
    }
}
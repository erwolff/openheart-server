package art.openhe.util

import art.openhe.util.ext.toClause


class DbUpdate
constructor(
    val id: String,
    private vararg val fieldAndValues: Pair<String, Any>?
) {

    private val updatePrefix = "{ \$set: { "
    private val updatePostfix = " } }"

    fun toQuery() =
        fieldAndValues.joinToString(prefix = updatePrefix, postfix = updatePostfix) {
            it?.let {
                "${it.first}:" + it.second.toClause()
            } ?: updatePrefix + updatePostfix
        }
}
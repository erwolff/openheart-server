package art.openhe.util.ext

import org.bson.types.ObjectId

/**
 * Extension function which applies the supplied lambda IFF the string is neither null nor empty
 */
fun <T> String?.letIfNotEmpty(lambda: (String) -> T): T? = if (this.isNullOrEmpty()) null else lambda(this)

fun <T> String?.letAsObjectId(lambda: (ObjectId) -> T): T? =
    if (this.isNullOrEmpty())
        null
    else
        try {
            lambda(ObjectId(this))
        } catch (e: IllegalArgumentException) {
            null
        }
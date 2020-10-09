package art.openhe.util.ext

import art.openhe.util.Mapper
import org.bson.types.ObjectId

/**
 * Extension function which applies the supplied lambda IFF the string is neither null nor empty
 */
fun <T> String?.letIfNotEmpty(lambda: (String) -> T): T? =
    if (this.isNullOrEmpty()) null
    else lambda(this)

fun <T> String?.letAsObjectId(lambda: (ObjectId) -> T): T? = this?.asObjectId()?.let{ lambda(it) }

fun <T> String.mapTo(toClass: Class<T>) = Mapper.fromString(this, toClass)

fun String.asObjectId() = ObjectId(this)
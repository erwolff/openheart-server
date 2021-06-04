package art.openhe.util.ext

import art.openhe.util.Mapper
import org.bson.types.ObjectId


val String.asObjectId: ObjectId?
    get() =
        if(!ObjectId.isValid(this)) null
        else ObjectId(this)

fun <T> String.deserializeAs(toClass: Class<T>) = Mapper.fromString(this, toClass)

/**
 * Returns true IFF this string is non-null and does not match any of the supplied enum values
 */
inline fun <reified T : Enum<T>> String.invalid(): Boolean = enumValues<T>().none { it.name.equals(this, ignoreCase = true) }

/**
 * Returns the enum value of the supplied string using the supplied enum
 */
inline fun <reified T : Enum<T>> String.asEnum(): T? = enumValues<T>().firstOrNull { it.name.equals(this, ignoreCase = true) }
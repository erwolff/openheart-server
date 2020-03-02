package art.openhe.util.ext

import art.openhe.util.Mapper


fun Any.toJson() = Mapper.toJson(this)

fun Any.toJsonString() = Mapper.toJsonString(this)
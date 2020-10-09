package art.openhe.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory


inline fun <reified T> T.logger(): Logger {
    if (T::class.isCompanion) {
        return LoggerFactory.getLogger(T::class.java.enclosingClass.simpleName)
    }
    return LoggerFactory.getLogger(T::class.java.simpleName)
}

fun Any.logError(message: () -> String): Unit =

    LoggerFactory.getLogger(this::class.java.simpleName).error(message())


fun Any.logInfo(message: () -> String): Unit =
    LoggerFactory.getLogger(this::class.java.simpleName).info(message())

fun Any.logWarn(message: () -> String): Unit =
    LoggerFactory.getLogger(this::class.java.simpleName).warn(message())


fun Any.logDebug(message: () -> String): Unit =
    LoggerFactory.getLogger(this::class.java.simpleName).debug(message())
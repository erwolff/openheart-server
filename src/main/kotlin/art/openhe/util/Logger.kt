package art.openhe.util

import org.slf4j.LoggerFactory


fun Any.logError(message: () -> String): Unit =
    LoggerFactory.getLogger(this::class.java.simpleName).error(message())

fun Any.logError(e: Exception, message: () -> String): Unit =
    LoggerFactory.getLogger(this::class.java.simpleName).error(message(), e)

fun Any.logInfo(message: () -> String): Unit =
    LoggerFactory.getLogger(this::class.java.simpleName).info(message())

fun Any.logWarn(message: () -> String): Unit =
    LoggerFactory.getLogger(this::class.java.simpleName).warn(message())

fun Any.logDebug(message: () -> String): Unit =
    LoggerFactory.getLogger(this::class.java.simpleName).debug(message())
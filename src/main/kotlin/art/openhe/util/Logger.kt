package art.openhe.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory


inline fun <reified T> T.logger(): Logger {
    if (T::class.isCompanion) {
        return LoggerFactory.getLogger(T::class.java.enclosingClass.simpleName)
    }
    return LoggerFactory.getLogger(T::class.java.simpleName)
}
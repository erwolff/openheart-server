package art.openhe.config

import javax.inject.Singleton


/**
 * Reads System environment properties
 */
@Singleton
class EnvConfig
constructor(
    private val props: Map<String, String> = System.getenv()
) {

    fun mongoUrl(): String =
        getValue("MONGO_URL") ?: "mongodb://127.0.0.1/openheart"

    fun redisUrl(): String =
        getValue("REDIS_URL") ?: "redis://localhost:6379/0"

    fun awsRegion(): String =
        getValue("AWS_REGION") ?: "us-east-2"

    fun awsAccessKey(): String =
        getValue("AWS_ACCESS_KEY") ?: ""

    fun awsSecretKey(): String =
        getValue("AWS_SECRET_KEY") ?: ""

    fun firebaseDbUrl(): String =
        getValue("FIREBASE_DB_URL") ?: ""

    fun welcomeLetterId(): String =
        getValue("WELCOME_LETTER_ID") ?: ""

    private fun getValue(key: String): String? = props[key]

}
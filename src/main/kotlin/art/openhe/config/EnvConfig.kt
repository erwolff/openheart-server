package art.openhe.config

import javax.inject.Singleton


@Singleton
class EnvConfig
constructor(private val props: Map<String, String> = System.getenv()) {

    fun mongoUrl(): String =
        getValue("MONGO_URL") ?: "mongodb://127.0.0.1/openheart"

    fun awsRegion(): String =
        getValue("AWS_REGION") ?: "us-east-2"

    fun awsAccessKey(): String =
        getValue("AWS_ACCESS_KEY") ?: ""

    fun awsSecretKey(): String =
        getValue("AWS_SECRET_KEY") ?: ""

    fun firebaseDbUrl(): String =
        getValue("FIREBASE_DB_URL") ?: ""

    private fun getValue(key: String): String? = props[key]

}
package art.openhe.util.ext

import org.jongo.MongoCollection
import java.lang.Exception


fun <T> MongoCollection.runQuery(lambda: (MongoCollection) -> T): T? =
    try { lambda(this) }
    catch (e: Exception) { null }
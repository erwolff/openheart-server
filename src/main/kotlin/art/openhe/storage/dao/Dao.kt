package art.openhe.storage.dao

import art.openhe.storage.Storage
import org.jongo.MongoCollection

/**
 * Marker interface for any dao
 */
open class Dao(val collection: MongoCollection) : Storage
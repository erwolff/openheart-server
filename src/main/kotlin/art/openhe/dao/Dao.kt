package art.openhe.dao

import org.jongo.MongoCollection

/**
 * Marker interface for any dao
 */
open class Dao(val collection: MongoCollection)
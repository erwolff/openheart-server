package art.openhe.dao

import art.openhe.model.Message
import art.openhe.util.UpdateQuery
import art.openhe.util.ext.letAsObjectId
import art.openhe.util.ext.positiveCountOrNull
import art.openhe.util.ext.runQuery
import org.bson.types.ObjectId
import org.jongo.Jongo
import org.jongo.MongoCollection
import org.jongo.Oid
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class MessageDao
@Inject constructor(@Named("messages") private val collection: MongoCollection) {


    fun save(message: Message): Message? =
        message.id.letAsObjectId {
            collection.runQuery { it.save(message)?.let { findById(message.id) } }
        }


    fun update(id: String, updateQuery: UpdateQuery): Message? =
        id.letAsObjectId {
            collection.runQuery { it.findAndModify(Oid.withOid(id)).with(updateQuery.toQuery()).returnNew().`as`(Message::class.java) }
        }


    fun delete(id: String): Int? =
        id.letAsObjectId { oid ->
            collection.runQuery { it.remove(oid)?.positiveCountOrNull() }
        }


    fun findById(id: String): Message? =
        id.letAsObjectId { oid ->
            collection.runQuery { it.findOne(oid).`as`(Message::class.java) }
        }
}
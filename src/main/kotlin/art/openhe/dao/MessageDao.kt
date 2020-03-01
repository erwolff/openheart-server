package art.openhe.dao

import art.openhe.model.Message
import art.openhe.util.UpdateQuery
import org.bson.types.ObjectId
import org.jongo.Jongo
import org.jongo.MongoCollection
import org.jongo.Oid
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessageDao
@Inject constructor(jongo: Jongo) {

    private val collection: MongoCollection = jongo.getCollection("messages")

    fun save(message: Message): Message? {
        collection.save(message)
        return findById(message.id)
    }

    fun update(id: String, updateQuery: UpdateQuery): Message? {
        return collection.findAndModify(Oid.withOid(id))
            .with(updateQuery.toQuery())
            .returnNew().`as`(Message::class.java)
    }

    //TODO: validate id is an ObjectId
    fun delete(id: String) {
        collection.remove(ObjectId(id))
    }

    //TODO: validate id is an ObjectId
    fun findById(id: String): Message? =
        collection.findOne(Oid.withOid(id)).`as`(Message::class.java)
}
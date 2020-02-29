package art.openhe.dao

import art.openhe.model.Message
import org.bson.types.ObjectId
import org.jongo.Jongo
import org.jongo.MongoCollection
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

    //TODO: validate id is an ObjectId
    fun findById(id: String): Message? =
        collection.findOne(ObjectId(id)).`as`(Message::class.java)

    //TODO: validate id is an ObjectId
    fun delete(id: String) {
        collection.remove(ObjectId(id))
    }
}
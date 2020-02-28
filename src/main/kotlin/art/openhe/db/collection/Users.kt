package art.openhe.db.collection

import art.openhe.model.User
import org.bson.types.ObjectId
import org.jongo.MongoCollection
import javax.inject.Singleton

@Singleton
class Users {

    //TODO this is a workaround currently
    lateinit var collection: MongoCollection

    fun save(user: User): User? {
        collection.save(user)
        return findById(user.id)
    }

    //TODO: validate id is an ObjectId
    fun findById(id: String): User? =
        collection.findOne(ObjectId(id)).`as`(User::class.java)

    //TODO: validate id is an ObjectId
    fun delete(id: String) {
        collection.remove(ObjectId(id))
    }
}
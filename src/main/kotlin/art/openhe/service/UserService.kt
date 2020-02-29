package art.openhe.service

import art.openhe.model.User
import org.bson.types.ObjectId
import org.jongo.Jongo
import org.jongo.MongoCollection
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserService
@Inject constructor(jongo: Jongo) {

    private val collection: MongoCollection = jongo.getCollection("users")

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
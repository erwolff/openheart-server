package art.openhe.dao

import art.openhe.model.User
import art.openhe.util.UpdateQuery
import art.openhe.util.ext.letAsObjectId
import art.openhe.util.ext.positiveCountOrNull
import art.openhe.util.ext.runQuery
import org.jongo.Jongo
import org.jongo.MongoCollection
import org.jongo.Oid
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class UserDao
@Inject constructor(@Named("users") internal val collection: MongoCollection) {


    fun save(user: User): User? =
        user.id.letAsObjectId {
            collection.runQuery { it.save(user)?.let { findById(user.id) } }
        }


    fun update(id: String, updateQuery: UpdateQuery): User? =
        id.letAsObjectId {
            collection.runQuery { it.findAndModify(Oid.withOid(id)).with(updateQuery.toQuery()).returnNew().`as`(User::class.java) }
        }


    fun delete(id: String): Int? =
        id.letAsObjectId { oid ->
            collection.runQuery { it.remove(oid).positiveCountOrNull() }
        }



    fun findById(id: String): User? =
        id.letAsObjectId { oid ->
            collection.runQuery { it.findOne(oid).`as`(User::class.java) }
        }
}
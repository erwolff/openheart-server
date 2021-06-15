package art.openhe.storage.dao

import art.openhe.model.User
import art.openhe.util.DbUpdate
import art.openhe.util.ext.asObjectId
import art.openhe.util.ext.positiveCountOrNull
import art.openhe.util.ext.runQuery
import org.jongo.MongoCollection
import org.jongo.Oid
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class UserDao
@Inject constructor(
    @Named("users") collection: MongoCollection
) : Dao(collection) {


    fun save(user: User): User? =
        user.id.asObjectId?.let {
            collection.runQuery { it.save(user)?.let { findById(user.id) } }
        }


    fun update(dbUpdate: DbUpdate): User? =
        dbUpdate.id.asObjectId?.let { oid ->
            collection.runQuery {
                it.findAndModify(Oid.withOid(oid.toHexString()))
                    .with(dbUpdate.toQuery())
                    .returnNew().`as`(User::class.java) }
        }


    fun delete(id: String): Int =
        id.asObjectId?.let { oid ->
            collection.runQuery { it.remove(oid).positiveCountOrNull() }
        } ?: 0


    fun findById(id: String): User? =
        id.asObjectId?.let { oid ->
            collection.runQuery { it.findOne(oid).`as`(User::class.java) }
        }
}
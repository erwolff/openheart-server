package art.openhe.dao

import art.openhe.model.Letter
import art.openhe.model.User
import art.openhe.util.DbUpdate
import art.openhe.util.ext.letAsObjectId
import art.openhe.util.ext.positiveCountOrNull
import art.openhe.util.ext.runQuery
import org.jongo.MongoCollection
import org.jongo.Oid
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class LetterDao
@Inject constructor(
    @Named("letters") collection: MongoCollection
) : Dao(collection) {


    fun save(letter: Letter): Letter? =
        letter.id.letAsObjectId {
            collection.runQuery { it.save(letter)?.let { findById(letter.id) } }
        }


    fun update(dbUpdate: DbUpdate): Letter? =
        dbUpdate.id.letAsObjectId { id ->
            collection.runQuery {
                it.findAndModify(Oid.withOid(id.toHexString()))
                    .with(dbUpdate.toQuery())
                    .returnNew().`as`(Letter::class.java) }
        }


    fun delete(id: String): Int? =
        id.letAsObjectId { oid ->
            collection.runQuery { it.remove(oid)?.positiveCountOrNull() }
        }


    fun findById(id: String): Letter? =
        id.letAsObjectId { oid ->
            collection.runQuery { it.findOne(oid).`as`(Letter::class.java) }
        }
}
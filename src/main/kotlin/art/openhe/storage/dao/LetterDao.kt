package art.openhe.storage.dao

import art.openhe.model.Letter
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
class LetterDao
@Inject constructor(
    @Named("letters") collection: MongoCollection
) : Dao(collection) {


    fun save(letter: Letter): Letter? =
        letter.id.asObjectId?.let {
            collection.runQuery { it.save(letter)?.let { findById(letter.id) } }
        }


    fun update(dbUpdate: DbUpdate): Letter? =
        dbUpdate.id.asObjectId?.let { oid ->
            collection.runQuery {
                it.findAndModify(Oid.withOid(oid.toHexString()))
                    .with(dbUpdate.toQuery())
                    .returnNew().`as`(Letter::class.java) }
        }


    fun delete(id: String): Int =
        id.asObjectId?.let { oid ->
            collection.runQuery { it.remove(oid)?.positiveCountOrNull() }
        } ?: 0


    fun findById(id: String): Letter? =
        id.asObjectId?.let { oid ->
            collection.runQuery { it.findOne(oid).`as`(Letter::class.java) }
        }
}
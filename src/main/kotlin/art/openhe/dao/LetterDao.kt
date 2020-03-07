package art.openhe.dao

import art.openhe.model.Letter
import art.openhe.util.UpdateQuery
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
@Inject constructor(@Named("letters") internal val collection: MongoCollection) {


    fun save(letter: Letter): Letter? =
        letter.id.letAsObjectId {
            collection.runQuery { it.save(letter)?.let { findById(letter.id) } }
        }


    fun update(id: String, updateQuery: UpdateQuery): Letter? =
        id.letAsObjectId {
            collection.runQuery { it.findAndModify(Oid.withOid(id)).with(updateQuery.toQuery()).returnNew().`as`(Letter::class.java) }
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
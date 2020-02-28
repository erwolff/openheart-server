package art.openhe.config

import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.mongodb.DB
import com.mongodb.MongoClient
import org.jongo.Jongo
import org.jongo.marshall.jackson.JacksonMapper
import javax.inject.Singleton

class ServiceProvider {

    @Singleton
    fun getJongo(): Jongo {
        val db: DB = MongoClient().getDB("openheart")
        val jongo = Jongo(db, JacksonMapper.Builder()
                .registerModule(KotlinModule())
                .build())

        //jongo.database.dropDatabase()
        ensureIndices(jongo)
        return jongo
    }

    private fun ensureIndices(jongo: Jongo) {
        jongo.getCollection("users").ensureIndex("{email:1}", "{unique:true,sparse:false}")
    }
}
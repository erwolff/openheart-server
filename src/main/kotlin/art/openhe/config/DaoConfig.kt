package art.openhe.config

import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.mongodb.DB
import com.mongodb.MongoClient
import org.jongo.Jongo
import org.jongo.MongoCollection
import org.jongo.marshall.jackson.JacksonMapper
import javax.inject.Named
import javax.inject.Singleton

class DaoConfig {

    @Singleton
    fun getJongo(): Jongo =
        Jongo(MongoClient().getDB("openheart"), JacksonMapper.Builder()
            .registerModule(KotlinModule())
            .build())


    @Singleton
    @Named("users")
    fun getUsersCollection(jongo: Jongo): MongoCollection {
        jongo.getCollection("users").ensureIndex("{googleId:1}", "{unique:true,sparse:false}")
        jongo.getCollection("users").ensureIndex("{email:1}", "{unique:true,sparse:false}")
        jongo.getCollection("users").ensureIndex("{createdTimestamp:1}")
        jongo.getCollection("users").ensureIndex("{lastReceivedLetterTimestamp:1}")
        return jongo.getCollection("users")
    }


    @Singleton
    @Named("letters")
    fun getLettersCollection(jongo: Jongo): MongoCollection {
        jongo.getCollection("letters").ensureIndex("{authorId:1}")
        jongo.getCollection("letters").ensureIndex("{recipientId:1}")
        jongo.getCollection("letters").ensureIndex("{createdTimestamp:1}")
        jongo.getCollection("letters").ensureIndex("{sentTimestamp:1}")
        jongo.getCollection("letters").ensureIndex("{writtenTimestamp:1}")
        return jongo.getCollection("letters")
    }
}
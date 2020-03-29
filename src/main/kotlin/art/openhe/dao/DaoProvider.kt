package art.openhe.dao

import art.openhe.config.EnvConfig
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.mongodb.MongoClient
import com.mongodb.MongoClientURI
import org.jongo.Jongo
import org.jongo.MongoCollection
import org.jongo.marshall.jackson.JacksonMapper
import javax.inject.Named
import javax.inject.Singleton

class DaoProvider {

    @Singleton
    fun getJongo(envConfig: EnvConfig): Jongo =
        Jongo(MongoClient(MongoClientURI(envConfig.mongoUrl())).getDB("openheart"), JacksonMapper.Builder()
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
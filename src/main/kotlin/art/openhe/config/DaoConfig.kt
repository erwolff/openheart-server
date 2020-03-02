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
        jongo.getCollection("users").ensureIndex("{email:1}", "{unique:true,sparse:false}")
        jongo.getCollection("users").ensureIndex("{createdTimestamp:1}")
        jongo.getCollection("users").ensureIndex("{lastReceivedMessageTimestamp:1}")
        return jongo.getCollection("users")
    }


    @Singleton
    @Named("messages")
    fun getMessagesCollection(jongo: Jongo): MongoCollection {
        jongo.getCollection("messages").ensureIndex("{authorId:1}")
        jongo.getCollection("messages").ensureIndex("{recipientId:1}")
        jongo.getCollection("messages").ensureIndex("{createdTimestamp:1}")
        return jongo.getCollection("messages")
    }
}
package art.openhe

import art.openhe.config.OpenHeartConfig
import art.openhe.db.collection.Users
import art.openhe.resource.HealthCheckResource
import art.openhe.resource.Resource
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.mongodb.DB
import com.mongodb.MongoClient
import io.dropwizard.Application
import io.dropwizard.setup.Environment
import io.novocaine.Novocaine
import org.jongo.Jongo
import org.jongo.marshall.jackson.JacksonMapper
import org.reflections.Reflections
import org.reflections.scanners.SubTypesScanner
import org.reflections.scanners.TypeAnnotationsScanner


fun main(args: Array<String>) {
    OpenHeartApplication().run(*args)
}

class OpenHeartApplication : Application<OpenHeartConfig>() {

    override fun run(configuration: OpenHeartConfig, environment: Environment) {
        println("Running ${configuration.name}")
        Novocaine.inject(this)
        environment.healthChecks().register(configuration.name, HealthCheckResource())
        registerResources(environment)
        createDatastore()
    }

    /**
     * Scans for all classes which implement the Resource interface in the resource package
     * and registers each with jersey
     */
    private fun registerResources(environment: Environment) {
        val reflections = Reflections("art.openhe.resource", SubTypesScanner(false), TypeAnnotationsScanner())
        val classes: Set<Class<out Resource?>> = reflections.getSubTypesOf(Resource::class.java)
        classes.forEach { environment.jersey().register(Novocaine.get(it)) }
    }

    private fun createDatastore() {
        val db: DB = MongoClient().getDB("openheart")
        val jongo = Jongo(db,
            JacksonMapper.Builder()
                .registerModule(KotlinModule())
                .build())

        //TODO this is a workaround currently
        //jongo.database.dropDatabase()
        val users = jongo.getCollection("users")
        users.ensureIndex("{email:1}", "{unique:true,sparse:false}");
        Novocaine.get(Users::class.java).collection = users

    }
}
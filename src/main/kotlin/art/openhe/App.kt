package art.openhe

import art.openhe.config.EnvConfig
import art.openhe.config.OpenHeartConfig
import art.openhe.queue.QueueProvider
import art.openhe.queue.consumer.SqsMessageConsumer
import art.openhe.resource.HealthCheckResource
import art.openhe.resource.Resource
import art.openhe.util.logInfo
import art.openhe.util.logger
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import io.dropwizard.Application
import io.dropwizard.setup.Environment
import io.novocaine.Novocaine
import org.reflections.Reflections
import org.reflections.scanners.SubTypesScanner
import org.reflections.scanners.TypeAnnotationsScanner
import javax.ws.rs.container.ContainerRequestFilter

/**
 * Entrypoint for the Openheart-server application
 */
fun main(args: Array<String>) {
    App().run(*args)
}

class App : Application<OpenHeartConfig>() {

    override fun run(configuration: OpenHeartConfig, environment: Environment) {
        logInfo { "Running ${configuration.name}" }
        Novocaine.inject(this)
        // TODO: Add support for something similar to Spring's @PostConstruct into Novocaine then move the below:
        registerResources(configuration, environment)
        registerConsumers()
        initFirebase()
    }

    /**
     * Scans for all classes which implement the Resource interface in the resource package
     * and registers each with jersey
     */
    private fun registerResources(configuration: OpenHeartConfig, environment: Environment) {
        // register the healthcheck resource
        environment.healthChecks().register(configuration.name, HealthCheckResource())

        // scan for and register all resources implementing Resource
        val reflections = Reflections("art.openhe.resource", SubTypesScanner(false), TypeAnnotationsScanner())
        val resources = reflections.getSubTypesOf(Resource::class.java)
        resources.forEach { environment.jersey().register(Novocaine.get(it)) }

        // scan for and register all request filters - these cannot be singletons from novocaine
        val filters = reflections.getSubTypesOf(ContainerRequestFilter::class.java)
        filters.forEach {
            environment.jersey().register(it)
        }
    }

    private fun registerConsumers() {
        // instantiate an aws sqs session
        val queueManager = Novocaine.get(QueueProvider::class.java)
        queueManager?.registerQueues()

        // scan for and register all consumers implementing SqsMessageConsumer
        val reflections = Reflections("art.openhe.queue.consumer", SubTypesScanner(false), TypeAnnotationsScanner())
        val classes = reflections.getSubTypesOf(SqsMessageConsumer::class.java)
        classes.forEach { Novocaine.get(it)?.register() }

        // start the consumers
        queueManager?.start()
    }

    private fun initFirebase() {
        val options = FirebaseOptions.Builder()
            .setCredentials(GoogleCredentials.getApplicationDefault())
            .setDatabaseUrl(Novocaine.get(EnvConfig::class.java).firebaseDbUrl())
            .build()

        FirebaseApp.initializeApp(options)
    }
}
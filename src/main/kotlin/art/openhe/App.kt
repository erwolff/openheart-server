package art.openhe

import art.openhe.config.OpenHeartConfig
import art.openhe.queue.QueueManager
import art.openhe.queue.consumer.SqsMessageConsumer
import art.openhe.resource.HealthCheckResource
import art.openhe.resource.Resource
import art.openhe.util.logger
import io.dropwizard.Application
import io.dropwizard.setup.Environment
import io.novocaine.Novocaine
import org.reflections.Reflections
import org.reflections.scanners.SubTypesScanner
import org.reflections.scanners.TypeAnnotationsScanner


fun main(args: Array<String>) {
    App().run(*args)
}

class App : Application<OpenHeartConfig>() {

    private val log = logger()

    override fun run(configuration: OpenHeartConfig, environment: Environment) {
        log.info("Running ${configuration.name}")
        Novocaine.inject(this)
        registerResources(configuration, environment)
        registerConsumers()
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
        val classes = reflections.getSubTypesOf(Resource::class.java)
        classes.forEach { environment.jersey().register(Novocaine.get(it)) }
    }

    private fun registerConsumers() {
        // instantiate an aws sqs session
        val queueManager = Novocaine.get(QueueManager::class.java)
        queueManager?.registerQueues()

        // scan for and register all consumers implementing SqsMessageConsumer
        val reflections = Reflections("art.openhe.queue.consumer", SubTypesScanner(false), TypeAnnotationsScanner())
        val classes = reflections.getSubTypesOf(SqsMessageConsumer::class.java)
        classes.forEach { Novocaine.get(it)?.register() }

        // start the consumers
        queueManager?.start()
    }
}
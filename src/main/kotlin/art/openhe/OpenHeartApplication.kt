package art.openhe

import art.openhe.config.OpenHeartConfig
import art.openhe.resource.HealthCheckResource
import art.openhe.resource.UserResource
import io.dropwizard.Application
import io.dropwizard.setup.Environment
import io.novocaine.Novocaine


fun main(args: Array<String>) {
    OpenHeartApplication().run(*args)
}

class OpenHeartApplication : Application<OpenHeartConfig>() {

    override fun run(configuration: OpenHeartConfig, environment: Environment) {
        println("Running ${configuration.name}")
        Novocaine.inject(this)
        environment.healthChecks().register(configuration.name, HealthCheckResource())

        /*environment.jersey().register(object : AbstractBinder() {
            override fun configure() {
                bind(Novocaine.get(AvatarResource::class.java)).to(AvatarResource::class.java)
            }
        })*/

        //environment.jersey().register(Novocaine.get(AvatarResource::class.java))
        environment.jersey().register(UserResource())
    }
}
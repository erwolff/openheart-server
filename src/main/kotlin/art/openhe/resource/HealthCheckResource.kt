package art.openhe.resource

import com.codahale.metrics.health.HealthCheck


class HealthCheckResource : HealthCheck() {

    override fun check() = Result.healthy()

}
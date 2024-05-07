package io.hamal.core.request

import io.hamal.core.adapter.auth.AuthGetPort
import io.hamal.core.component.WorkerPool
import io.hamal.core.security.SecurityContext
import io.hamal.lib.common.domain.Limit.Companion.Limit
import io.hamal.repository.api.RequestCmdRepository
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.stereotype.Service
import kotlin.time.Duration.Companion.milliseconds

@Service
class RequestInvoker private constructor(
    private val requestCmdRepository: RequestCmdRepository,
    private val requestRegistry: RequestRegistry,
    private val workerPool: WorkerPool,
    private val authGet: AuthGetPort
) : ApplicationListener<ContextRefreshedEvent> {

    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        workerPool.atFixedDelay(1.milliseconds) {
            requestCmdRepository.next(Limit(10)).forEach { requested ->
                try {
                    val handleRequested = requestRegistry[requested::class]
                    SecurityContext.with(authGet(requested.requestedBy)) {
                        handleRequested(requested)
                    }
                    requestCmdRepository.complete(requested.requestId)
                } catch (t: Throwable) {
                    t.printStackTrace()
                    requestCmdRepository.fail(requested.requestId)
                }
            }
        }
    }
}

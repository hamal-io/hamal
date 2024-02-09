package io.hamal.core.request

import io.hamal.core.component.Async
import io.hamal.lib.common.domain.Limit
import io.hamal.repository.api.RequestCmdRepository
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.stereotype.Service
import kotlin.time.Duration.Companion.milliseconds

@Service
class RequestInvoker private constructor(
    private val requestCmdRepository: RequestCmdRepository,
    private val requestRegistry: RequestRegistry,
    private val async: Async
) : ApplicationListener<ContextRefreshedEvent> {

    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        async.atFixedRate(1.milliseconds) {
            requestCmdRepository.next(Limit(10))
                .forEach { request ->
                    try {
                        val handleRequest = requestRegistry[request::class]
                        handleRequest(request)
                        requestCmdRepository.complete(request.id)
                    } catch (t: Throwable) {
                        t.printStackTrace()
                        requestCmdRepository.fail(request.id)
                    }
                }
        }
    }
}

package io.hamal.core.req

import io.hamal.core.component.Async
import io.hamal.repository.api.RequestCmdRepository
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.stereotype.Service
import kotlin.time.Duration.Companion.milliseconds

@Service
class ReqInvoker private constructor(
    private val reqCmdRepository: RequestCmdRepository,
    private val regRegistry: ReqRegistry,
    private val async: Async
) : ApplicationListener<ContextRefreshedEvent> {

    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        async.atFixedRate(1.milliseconds) {
            reqCmdRepository.next(10)
                .forEach { req ->
                    try {
                        regRegistry[req::class](req)
                        reqCmdRepository.complete(req.id)
                    } catch (t: Throwable) {
                        t.printStackTrace()
                        reqCmdRepository.fail(req.id)
                    }
                }
        }
    }
}

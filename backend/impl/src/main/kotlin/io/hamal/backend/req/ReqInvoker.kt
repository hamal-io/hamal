package io.hamal.backend.req

import io.hamal.backend.repository.api.ReqCmdRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class ReqInvoker private constructor(
    private val reqCmdRepository: ReqCmdRepository,
    private val regRegistry: ReqRegistry,
) {
    @Scheduled(fixedRate = 1, initialDelay = 100, timeUnit = TimeUnit.MILLISECONDS)
    fun run() {
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

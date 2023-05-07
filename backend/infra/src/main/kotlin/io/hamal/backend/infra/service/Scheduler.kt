package io.hamal.backend.infra.service

import io.hamal.backend.core.logger
import org.springframework.stereotype.Service


@Service
class Scheduler {
    private val log = logger(Scheduler::class)

//    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.SECONDS)
//    fun schedule() {
//        log.debug("scheduler")
//    }

}
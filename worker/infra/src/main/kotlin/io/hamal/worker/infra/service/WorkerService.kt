package io.hamal.worker.infra.service

import io.hamal.lib.sdk.DefaultHamalSdk
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.util.concurrent.TimeUnit

@Service
class WorkerService
@Autowired constructor(
    val restTemplate: RestTemplate
) {


    data class Test(
        val id: String
    )

    @Scheduled(initialDelay = 1, fixedDelay = 1, timeUnit = TimeUnit.SECONDS)
    fun run() {
        val jobs = DefaultHamalSdk.queueService().poll()
        println(jobs)
    }

}
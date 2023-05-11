package io.hamal.worker.infra.service

import io.hamal.lib.domain.api.ApiWorkerJobs
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.postForEntity
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

        val response: ResponseEntity<ApiWorkerJobs> =
            restTemplate.postForEntity<ApiWorkerJobs>("http://localhost:8084/v1/dequeue")
        println(response.body?.jobs)

        val jobs = response.body?.jobs ?: listOf()
        if (jobs.isNotEmpty()) {
            val job = jobs.first()
            restTemplate.postForEntity<Any>("http://localhost:8084/v1/jobs/${job.id}/complete")
        }
    }

}
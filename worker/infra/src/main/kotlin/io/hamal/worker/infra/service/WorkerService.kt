package io.hamal.worker.infra.service

import io.hamal.lib.sdk.DefaultHamalSdk
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class WorkerService {

    @Scheduled(initialDelay = 1, fixedDelay = 1, timeUnit = TimeUnit.SECONDS)
    fun run() {
        val jobs = DefaultHamalSdk.jobService().poll()
        jobs.jobs.forEach { job ->
            println("Processing job $job")
            job.tasks.forEach { task ->
                println("Start executing task $task")
                println("Finish executing task $task")
            }
            DefaultHamalSdk.jobService().complete(job.id)
        }
    }

}
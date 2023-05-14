package io.hamal.worker.infra.service

import io.hamal.lib.script.impl.DefaultSandbox
import io.hamal.lib.script.impl.interpreter.Environment
import io.hamal.lib.sdk.DefaultHamalSdk
import io.hamal.lib.sdk.domain.ApiWorkerScriptTask
import io.hamal.worker.infra.adapter.WorkerExtensionLoader
import jakarta.annotation.PostConstruct
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.io.File
import java.util.concurrent.TimeUnit

@Service
class WorkerService {

    @PostConstruct
    fun postConstruct() {

        println("Worker active")

        val entryPointLoader = WorkerExtensionLoader.DefaultImpl()
        val x =
            entryPointLoader.load(File("/home/ddymke/Repo/hamal/worker/extension/impl/web3/build/libs/extension-starter.jar"))

        x.functionFactories()
            .map { it.create() }
            .forEach { it() }


    }

    @Scheduled(initialDelay = 1, fixedDelay = 1, timeUnit = TimeUnit.SECONDS)
    fun run() {
        val jobs = DefaultHamalSdk.jobService().poll()
        jobs.jobs.forEach { job ->
            println("Processing job $job")
            job.tasks.forEach { task ->
                println("Start executing task $task")

                require(task is ApiWorkerScriptTask)
                println("Executing hamal script: ${task.code}")
                val sandbox = DefaultSandbox(Environment())
                val result = sandbox.eval(task.code.value)
                println("RESULT: $result")

                println("Finish executing task $task")
            }
            DefaultHamalSdk.jobService().complete(job.id)
        }
    }

}
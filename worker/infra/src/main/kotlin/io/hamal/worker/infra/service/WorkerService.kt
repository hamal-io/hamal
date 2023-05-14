package io.hamal.worker.infra.service

import io.hamal.lib.script.api.native_.NativeFunction
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

    private val nativeFunctions = mutableListOf<NativeFunction>()

    @PostConstruct
    fun postConstruct() {

        println("Worker active")

        val entryPointLoader = WorkerExtensionLoader.DefaultImpl()

        val s =
            entryPointLoader.load(
                File("/home/ddymke/Repo/hamal/worker/extension/impl/web3/build/libs/extension-starter.jar")
            )
        nativeFunctions.addAll(s.functionFactories())

        val x =
            entryPointLoader.load(
                File("/home/ddymke/Repo/hamal/worker/extension/impl/web3/build/libs/extension-web3.jar")
            )

        nativeFunctions.addAll(x.functionFactories())

//        x.functionFactories()

//            .map { it.create() }
//            .forEach { it(DefaultContext()) }


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
                val env = Environment()

                nativeFunctions.forEach { nativeFunction ->
                    env.register(nativeFunction)
                }

                val sandbox = DefaultSandbox(env)
                val result = sandbox.eval(task.code.value)
                println("RESULT: $result")

                println("Finish executing task $task")
            }
            DefaultHamalSdk.jobService().complete(job.id)
        }
    }

}
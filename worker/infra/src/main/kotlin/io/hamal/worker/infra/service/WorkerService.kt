package io.hamal.worker.infra.service

import io.hamal.lib.script.api.Environment
import io.hamal.lib.script.api.native_.FunctionValue
import io.hamal.lib.script.impl.DefaultSandbox
import io.hamal.lib.script.impl.eval.RootEnvironment
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

    private val functionValues = mutableListOf<FunctionValue>()

    //FIXME introduce WorkerExtensionEnvironment as a wrapper around native env
    private val extensionEnvironments = mutableListOf<Environment>()

    @PostConstruct
    fun postConstruct() {

        println("Worker active")

        val entryPointLoader = WorkerExtensionLoader.DefaultImpl()

//        val s =
//            entryPointLoader.load(
//                File("/home/ddymke/Repo/hamal/worker/extension/impl/web3/build/libs/extension-starter.jar")
//            )
//        nativeFunctions.addAll(s.functionFactories())

        val x =
            entryPointLoader.load(
                File("/home/ddymke/Repo/hamal/worker/extension/impl/web3/build/libs/extension-web3.jar")
            )

        functionValues.addAll(x.functionFactories())

        extensionEnvironments.addAll(x.environments())

//        x.functionFactories()

//            .map { it.create() }
//            .forEach { it(DefaultContext()) }


    }

    @Scheduled(initialDelay = 100, fixedDelay = 100, timeUnit = TimeUnit.MILLISECONDS)
    fun run() {
        val jobs = DefaultHamalSdk.jobService().poll()
        jobs.jobs.forEach { job ->
            println("Processing job $job")
            job.tasks.forEach { task ->
                println("Start executing task $task")

                require(task is ApiWorkerScriptTask)
                println("Executing hamal script: ${task.code}")
                val env = RootEnvironment()

                functionValues.forEach { nativeFunction ->
                    env.add(nativeFunction)
                }

                extensionEnvironments.forEach { environment ->
                    env.add(environment)
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
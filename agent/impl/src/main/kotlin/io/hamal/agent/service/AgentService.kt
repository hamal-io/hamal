package io.hamal.agent.service

import io.hamal.agent.adapter.ExtensionLoader
import io.hamal.lib.script.api.value.EnvironmentValue
import io.hamal.lib.script.api.value.FunctionValue
import io.hamal.lib.script.api.value.Identifier
import io.hamal.lib.script.impl.DefaultSandbox
import io.hamal.lib.script.impl.builtin.AssertFunction
import io.hamal.lib.script.impl.builtin.RequireFunction
import io.hamal.lib.sdk.DefaultHamalSdk
import io.hamal.lib.sdk.service.ExecService
import jakarta.annotation.PostConstruct
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.io.File
import java.util.concurrent.TimeUnit

@Service
class AgentService {

    private val functionValues = mutableListOf<FunctionValue>()

    //FIXME introduce WorkerExtensionEnvironment as a wrapper around native env
    private val extensionEnvironments = mutableListOf<EnvironmentValue>()

    @PostConstruct
    fun postConstruct() {

        println("Agent active")

        val entryPointLoader = ExtensionLoader.DefaultImpl()

//        val s =
//            entryPointLoader.load(
//                File("/home/ddymke/Repo/hamal/worker/extension/impl/web3/build/libs/extension-starter.jar")
//            )
//        nativeFunctions.addAll(s.functionFactories())

//        val x =
//            entryPointLoader.load(
//                File("/home/ddymke/Repo/hamal/agent/extension/impl/web3/build/libs/extension-web3.jar")
//            )
//
//
//        extensionEnvironments.add(x.create()) // FIXME store the factory not the environment - a new environment must be created when calling require each times

        val log =
            entryPointLoader.load(File("/home/ddymke/Repo/hamal/agent/extension/std/log/build/libs/extension-std-log.jar"))
        extensionEnvironments.add(log.create()) // FIXME store the factory not the environment - a new environment must be created when calling require each times

        val hamal =
            entryPointLoader.load(File("/home/ddymke/Repo/hamal/agent/extension/std/hamal/build/libs/extension-std-hamal.jar"))
        extensionEnvironments.add(hamal.create()) // FIXME store the factory not the environment - a new environment must be created when calling require each times

        val debug =
            entryPointLoader.load(File("/home/ddymke/Repo/hamal/agent/extension/std/debug/build/libs/extension-std-debug.jar"))
        extensionEnvironments.add(debug.create()) // FIXME store the factory not the environment - a new environment must be created when calling require each times

//        x.functionFactories()

//            .map { it.create() }
//            .forEach { it(DefaultContext()) }


    }

    @Scheduled(initialDelay = 100, fixedDelay = 100, timeUnit = TimeUnit.MILLISECONDS)
    fun run() {
        DefaultHamalSdk.execService()
            .poll()
            .requests.forEach { request ->

                println("$request")
                println("Execute: ${request.id} - ${request.correlation}")
                println("State: ${request.statePayload}")

                val counter = (request.statePayload?.bytes?.let { String(it) } ?: "0").toInt()
                println(counter)

                val env = EnvironmentValue(
                    identifier = Identifier("_G"),
                    values = mapOf(
                        AssertFunction.identifier to AssertFunction,
                        RequireFunction.identifier to RequireFunction
                    )
                )


                extensionEnvironments.forEach { environment ->
                    env.addGlobal(environment.identifier, environment)
                }

                val sandbox = DefaultSandbox(env)
                val result = sandbox.eval(request.code.value)
                println("RESULT: $result")

                println("Finish executing task ${request.id}")

                DefaultHamalSdk.execService().complete(
                    request.id, ExecService.StateAfterCompletion(
                        contentType = "application/json",
                        bytes = (counter + 1).toString().toByteArray()
                    )
                )

            }
    }

}
package io.hamal.agent.service

import io.hamal.agent.adapter.ExtensionLoader
import io.hamal.lib.common.value.EnvValue
import io.hamal.lib.common.value.FuncValue
import io.hamal.lib.common.value.IdentValue
import io.hamal.lib.script.impl.DefaultSandbox
import io.hamal.lib.sdk.DefaultHamalSdk
import io.hamal.lib.sdk.service.ExecService
import jakarta.annotation.PostConstruct
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.io.File
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

@Service
class AgentService {

    private val functionValues = mutableListOf<FuncValue>()

    //FIXME introduce WorkerExtensionEnvironment as a wrapper around native env
    private val extensionEnvironments = mutableListOf<EnvValue>()

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

        val sys =
            entryPointLoader.load(File("/home/ddymke/Repo/hamal/agent/extension/std/sys/build/libs/extension-std-sys.jar"))
        extensionEnvironments.add(sys.create()) // FIXME store the factory not the environment - a new environment must be created when calling require each times

        val debug =
            entryPointLoader.load(File("/home/ddymke/Repo/hamal/agent/extension/std/debug/build/libs/extension-std-debug.jar"))
        extensionEnvironments.add(debug.create()) // FIXME store the factory not the environment - a new environment must be created when calling require each times

//        x.functionFactories()

//            .map { it.create() }
//            .forEach { it(DefaultContext()) }


    }

    @Scheduled(initialDelay = 100, fixedDelay = 1000, timeUnit = TimeUnit.MILLISECONDS)
    fun run() {
        CompletableFuture.runAsync {
            DefaultHamalSdk.execService()
                .poll()
                .requests.forEach { request ->

                    println("${request.inputs} - ${request.inputs.value}")
                    println("${request.secrets} - ${request.secrets.value.size}")

//                println("$request")
//                println("Execute: ${request.id} - ${request.correlation}")
//                println("State: ${request.statePayload}")
//
                    val counter = (request.statePayload?.bytes?.let { String(it) } ?: "0").toInt()
//                println(counter)

                    val env = EnvValue(
                        ident = IdentValue("_G"),
                        values = mapOf(
                            // FIXME
//                            AssertFunction.identifier to AssertFunction,
//                            RequireFunction.identifier to RequireFunction
                        )
                    )


                    extensionEnvironments.forEach { environment ->
                        env.addGlobal(environment.ident, environment)
                    }

                    val sandbox = DefaultSandbox(env)
                    val result = sandbox.eval(request.code.value)
//                println("RESULT: $result")
//
//                println("Finish executing task ${request.id}")

                    DefaultHamalSdk.execService().complete(
                        request.id, ExecService.StateAfterCompletion(
                            contentType = "application/json",
                            bytes = (counter + 1).toString().toByteArray()
                        )
                    )

                }
        }
    }

}
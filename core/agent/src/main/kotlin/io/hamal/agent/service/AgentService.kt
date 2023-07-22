package io.hamal.agent.service

import io.hamal.lib.domain.State
import io.hamal.lib.kua.ExitException
import io.hamal.lib.kua.SandboxFactory
import io.hamal.lib.kua.value.ErrorValue
import io.hamal.lib.kua.value.NumberValue
import io.hamal.lib.kua.value.TableValue
import io.hamal.lib.sdk.DefaultHamalSdk
import io.hamal.lib.sdk.HttpTemplateSupplier
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

@Service
class AgentService(
    private val httpTemplateSupplier: HttpTemplateSupplier,
    private val sandboxFactory: SandboxFactory
) {

    private val sdk by lazy { DefaultHamalSdk(httpTemplateSupplier()) }

    @Scheduled(initialDelay = 1, fixedDelay = 1, timeUnit = TimeUnit.MILLISECONDS)
    fun run() {
        CompletableFuture.runAsync {
            sdk.execService()
                .poll()
                .execs.forEach { request ->

                    try {
                        val sandbox = sandboxFactory.create()
                        sandbox.runCode(request.code)
//                        println("${request.inputs} - ${request.inputs.value}")
//
//                        val result = sandbox.eval(request.code.value)
//                        println("RESULT: $result")
//
//                        if (result is ErrorValue) {
//                            sdk.execService().fail(request.id, result)
//                        }
////
                        //FIXME close sandbox after usage

                        sdk.execService().complete(
                            request.id, State(TableValue())
                        )
                    } catch (e: ExitException) {
                        println("Exit ${e.status}")
                        if (e.status == NumberValue(0.0)) {
                            sdk.execService().complete(
                                request.id, State(TableValue())
                            )
                        } else {
                            sdk.execService().fail(request.id, ErrorValue(e.message ?: "Unknown error"))
                        }
//
                    } catch (t: Throwable) {
                        t.printStackTrace()

                        sdk.execService().fail(request.id, ErrorValue(t.message ?: "Unknown error"))
                    }
                }
        }
    }

}
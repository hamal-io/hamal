package io.hamal.agent.service

import io.hamal.agent.component.Async
import io.hamal.lib.domain.State
import io.hamal.lib.kua.ExitError
import io.hamal.lib.kua.SandboxFactory
import io.hamal.lib.kua.value.ErrorValue
import io.hamal.lib.kua.value.NumberValue
import io.hamal.lib.kua.value.TableValue
import io.hamal.lib.sdk.DefaultHamalSdk
import io.hamal.lib.sdk.HttpTemplateSupplier
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Service
import kotlin.time.Duration.Companion.milliseconds

@Service
class AgentService(
    private val httpTemplateSupplier: HttpTemplateSupplier,
    private val sandboxFactory: SandboxFactory,
    private val async: Async
) {

    private val sdk by lazy { DefaultHamalSdk(httpTemplateSupplier()) }

    @PostConstruct
    fun setup() {
        var counter = 0;
        async.atFixedRate(1.milliseconds) {
            sdk.execService()
                .poll()
                .execs.forEach { request ->

                    try {
                        println(Thread.currentThread().name)

                        sandboxFactory.create().use {
                            it.runCode(request.code)
                        }
//                        println("${request.inputs} - ${request.inputs.value}")
//
//                        val result = sandbox.eval(request.code.value)
//                        println("RESULT: $result")
//
//                        if (result is ErrorValue) {
//                            sdk.execService().fail(request.id, result)
//                        }
                        counter++

                        println("Counter: ${counter}")
////
                        //FIXME close sandbox after usage

                        sdk.execService().complete(
                            request.id, State(TableValue())
                        )
                    } catch (e: ExitError) {
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
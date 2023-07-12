package io.hamal.agent.service

import io.hamal.lib.domain.State
import io.hamal.lib.domain.vo.Content
import io.hamal.lib.domain.vo.ContentType
import io.hamal.lib.script.api.Sandbox
import io.hamal.lib.script.api.value.ErrorValue
import io.hamal.lib.sdk.DefaultHamalSdk
import io.hamal.lib.sdk.HttpTemplateSupplier
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

@Service
class AgentService(
    private val httpTemplateSupplier: HttpTemplateSupplier,
    private val sandbox: Sandbox
) {

    private val sdk by lazy { DefaultHamalSdk(httpTemplateSupplier()) }

    @Scheduled(initialDelay = 1, fixedDelay = 1, timeUnit = TimeUnit.MILLISECONDS)
    fun run() {
        CompletableFuture.runAsync {
            sdk.execService()
                .poll()
                .execs.forEach { request ->

                    try {

                        println("${request.inputs} - ${request.inputs.value}")

                        val result = sandbox.eval(request.code.value)
                        println("RESULT: $result")

                        if (result is ErrorValue) {
                            sdk.execService().fail(request.id, result)
                        }
//
                        sdk.execService().complete(
                            request.id, State(
                                contentType = ContentType("application/json"),
                                content = Content("")
                            )
                        )

                    } catch (t: Throwable) {
                        t.printStackTrace()
                    }
                }
        }
    }

}
package io.hamal.agent.service

import io.hamal.agent.component.AgentAsync
import io.hamal.lib.domain.State
import io.hamal.lib.kua.ExitError
import io.hamal.lib.kua.KuaError
import io.hamal.lib.kua.SandboxFactory
import io.hamal.lib.kua.value.*
import io.hamal.lib.sdk.DefaultHamalSdk
import io.hamal.lib.sdk.HttpTemplateSupplier
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.milliseconds

@Service
class AgentService(
    private val httpTemplateSupplier: HttpTemplateSupplier,
    private val sandboxFactory: SandboxFactory,
    private val async: AgentAsync
) {


    @Scheduled(initialDelay = 1, timeUnit = TimeUnit.SECONDS, fixedRate = Int.MAX_VALUE.toLong())
    fun run() {
        val sdk = DefaultHamalSdk(httpTemplateSupplier())
        async.atFixedRate(1.milliseconds) {
            sdk.execService()
                .poll()
                .execs.forEach { request ->
                    try {

                        lateinit var stateResult: State

                        sandboxFactory.create().use { sb ->
                            sb.run { state ->
                                val ctx = state.tableCreateMap(1)
                                val funcState = state.tableCreateMap(1)

                                request.state.value.forEach { entry ->
                                    when (val value = entry.value) {
                                        is BooleanValue -> funcState[entry.key] = value
                                        is NumberValue -> funcState[entry.key] = value
                                        is DecimalValue -> funcState[entry.key] =
                                            NumberValue(value.value.toDouble()) // FIXME!
                                        is StringValue -> funcState[entry.key] = value
                                        else -> TODO()
                                    }
                                }
                                ctx["state"] = funcState
                                state.setGlobal("ctx", ctx)
                            }
                            sb.runCode(request.code)

                            sb.run { state ->
                                val ctx = state.getGlobalTableMap("ctx")
                                val funcState = ctx.getTableMap("state")

                                val stateMap = mutableMapOf<StringValue, SerializableValue>()

                                state.bridge.pushNil()
                                while (state.bridge.tableNext(funcState.index)) {
                                    val k = state.getStringValue(-2)
                                    val v = state.getAnyValue(-1)

                                    when (val n = v.value) {
                                        is NumberValue -> stateMap[k] = n
                                        is StringValue -> stateMap[k] = n
                                        else -> TODO()
                                    }

                                    state.bridge.pop(1)
                                }

                                stateResult = State(
                                    TableValue(stateMap)
                                )
                            }
                        }

                        sdk.execService().complete(
                            request.id, stateResult
                        )
                    } catch (kua: KuaError) {
                        val e = kua.cause
                        if (e is ExitError) {
                            println("Exit ${e.status.value}")
                            if (e.status == NumberValue(0.0)) {
                                sdk.execService().complete(
                                    request.id, State()
                                )
                            } else {
                                sdk.execService().fail(request.id, ErrorValue(e.message ?: "Unknown error"))
                            }
                        } else {
                            throw kua
                        }
                    } catch (t: Throwable) {
                        t.printStackTrace()

                        sdk.execService().fail(request.id, ErrorValue(t.message ?: "Unknown error"))
                    }
                }
        }
    }

}
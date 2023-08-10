package io.hamal.runner.service

import io.hamal.lib.domain.Event
import io.hamal.lib.domain.EventInvocation
import io.hamal.lib.domain.State
import io.hamal.lib.kua.ExitError
import io.hamal.lib.kua.ExtensionError
import io.hamal.lib.kua.function.Function1In0Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.table.TableMapValue
import io.hamal.lib.kua.value.*
import io.hamal.lib.sdk.DefaultHamalSdk
import io.hamal.lib.sdk.HttpTemplateSupplier
import io.hamal.runner.component.RunnerAsync
import io.hamal.runner.config.SandboxFactory
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.milliseconds

private val log = LoggerFactory.getLogger(RunnerService::class.java)

@Service
class RunnerService(
    private val httpTemplateSupplier: HttpTemplateSupplier,
    private val sandboxFactory: SandboxFactory,
    private val async: RunnerAsync
) {
    @Scheduled(initialDelay = 1, timeUnit = TimeUnit.SECONDS, fixedRate = Int.MAX_VALUE.toLong())
    fun run() {
        val sdk = DefaultHamalSdk(httpTemplateSupplier())
        async.atFixedRate(1.milliseconds) {
            sdk.execService()
                .poll()
                .execs.forEach { exec ->
                    try {
                        log.debug("Picked up: {}", exec.id)

                        lateinit var stateResult: State
                        val eventsToEmit = mutableListOf<Event>()

                        sandboxFactory.create(exec).use { sb ->
                            sb.run { state ->
                                val ctx = state.tableCreateMap(1)

                                sb.native.pushFunctionValue(EmitEventFunction(eventsToEmit))
                                sb.native.tabletSetField(ctx.index, "emit")

                                val funcState = state.tableCreateMap(1)
                                exec.state.value.forEach { entry ->
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
                                log.debug("Injected state into context")


                                val invocation = exec.invocation
                                val events = state.tableCreateArray(0)
                                if (invocation is EventInvocation) {
                                    invocation.events.forEach { evt ->
                                        val evtTable = state.tableCreateMap(1)
                                        evt.value.forEach { payload ->
                                            when (val v = payload.value) {
                                                is StringValue -> evtTable[payload.key] = v
                                                is NumberValue -> evtTable[payload.key] = v
                                                else -> TODO()
                                            }
                                        }

                                        sb.native.tableAppend(events.index)
                                    }
                                }

                                ctx["events"] = events
                                log.debug("Injected {} events into context", events.length())
                                state.setGlobal("ctx", ctx)
                            }

                            log.debug("Start code execution")
                            sb.load(exec.code)
                            log.debug("Code execution finished normally")


                            sb.run { state ->
                                val ctx = state.getGlobalTableMap("ctx")
                                val funcState = ctx.getTableMap("state")

                                val stateMap = mutableMapOf<StringValue, SerializableValue>()

                                state.native.pushNil()
                                while (state.native.tableNext(funcState.index)) {
                                    val k = state.getStringValue(-2)
                                    val v = state.getAnyValue(-1)

                                    when (val n = v.value) {
                                        is NumberValue -> stateMap[k] = n
                                        is StringValue -> stateMap[k] = n
                                        else -> TODO()
                                    }

                                    state.native.pop(1)
                                }

                                stateResult = State(TableValue(stateMap))
                            }
                        }

                        sdk.execService().complete(exec.id, stateResult, eventsToEmit)
                        log.debug("Completed exec: {}", exec.id)

                    } catch (kua: ExtensionError) {
                        kua.printStackTrace()

                        val e = kua.cause
                        if (e is ExitError) {
                            if (e.status == NumberValue(0.0)) {
                                sdk.execService().complete(exec.id, State(), listOf())
                                log.debug("Completed exec: {}", exec.id)
                            } else {
                                sdk.execService().fail(exec.id, ErrorValue(e.message ?: "Unknown reason"))
                                log.debug("Failed exec: {}", exec.id)
                            }
                        } else {
                            sdk.execService().fail(exec.id, ErrorValue(kua.message ?: "Unknown reason"))
                            log.debug("Failed exec: {}", exec.id)
                        }
                    } catch (t: Throwable) {
                        t.printStackTrace()
                        sdk.execService().fail(exec.id, ErrorValue(t.message ?: "Unknown reason"))
                        log.debug("Failed exec: {}", exec.id)
                    }
                }
        }
    }
}

class EmitEventFunction(
    val eventsCollector: MutableList<Event>
) : Function1In0Out<TableMapValue>(
    FunctionInput1Schema(TableMapValue::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: TableMapValue) {
        ctx.pushNil()

        val eventMap = mutableMapOf<StringValue, SerializableValue>()

        while (ctx.state.native.tableNext(arg1.index)) {
            val k = ctx.getStringValue(-2)
            val v = ctx.getAnyValue(-1)
            when (val n = v.value) {
                is NumberValue -> eventMap[k] = n
                is StringValue -> eventMap[k] = n
                else -> TODO()
            }
            ctx.native.pop(1)
        }


        // FIXME make sure topic is set and string
        require(eventMap.containsKey(StringValue("topic")))

        eventsCollector.add(Event(TableValue(eventMap)))
    }
}
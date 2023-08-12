package io.hamal.runner.service

import io.hamal.lib.domain.Event
import io.hamal.lib.domain.EventInvocation
import io.hamal.lib.domain.State
import io.hamal.lib.kua.AssertionError
import io.hamal.lib.kua.ExitError
import io.hamal.lib.kua.ExtensionError
import io.hamal.lib.kua.function.Function1In0Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.table.TableMap
import io.hamal.lib.kua.type.*
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

                                sb.native.pushFunction(EmitEventFunction(eventsToEmit))
                                sb.native.tabletSetField(ctx.index, "emit")

                                val funcState = state.tableCreateMap(1)
                                exec.state.value.forEach { entry ->
                                    when (val value = entry.value) {
                                        is BooleanType -> funcState[entry.key] = value
                                        is DoubleType -> funcState[entry.key] = value
                                        is StringType -> funcState[entry.key] = value
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
                                                is StringType -> evtTable[payload.key] = v
                                                is DoubleType -> evtTable[payload.key] = v
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

                                val stateMap = mutableMapOf<StringType, SerializableType>()

                                state.native.pushNil()
                                while (state.native.tableNext(funcState.index)) {
                                    val k = state.getStringValue(-2)
                                    val v = state.getAny(-1)

                                    when (val n = v.value) {
                                        is DoubleType -> stateMap[k] = n
                                        is StringType -> stateMap[k] = n
                                        else -> TODO()
                                    }

                                    state.native.pop(1)
                                }

                                stateResult = State(TableType(stateMap))
                            }
                        }

                        sdk.execService().complete(exec.id, stateResult, eventsToEmit)
                        log.debug("Completed exec: {}", exec.id)

                    } catch (kua: ExtensionError) {
                        kua.printStackTrace()

                        val e = kua.cause
                        if (e is ExitError) {
                            if (e.status == DoubleType(0.0)) {
                                sdk.execService().complete(exec.id, State(), listOf())
                                log.debug("Completed exec: {}", exec.id)
                            } else {
                                sdk.execService().fail(exec.id, ErrorType(e.message ?: "Unknown reason"))
                                log.debug("Failed exec: {}", exec.id)
                            }
                        } else {
                            sdk.execService().fail(exec.id, ErrorType(kua.message ?: "Unknown reason"))
                            log.debug("Failed exec: {}", exec.id)
                        }
                    } catch (a: AssertionError) {
                        a.printStackTrace()
                        sdk.execService().fail(exec.id, ErrorType(a.message ?: "Unknown reason"))
                        log.debug("Assertion error: {} - {}", exec.id, a.message)
                    } catch (t: Throwable) {
                        t.printStackTrace()
                        sdk.execService().fail(exec.id, ErrorType(t.message ?: "Unknown reason"))
                        log.debug("Failed exec: {}", exec.id)
                    }
                }
        }
    }
}

class EmitEventFunction(
    val eventsCollector: MutableList<Event>
) : Function1In0Out<TableMap>(
    FunctionInput1Schema(TableMap::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: TableMap) {
        ctx.pushNil()

        val eventMap = mutableMapOf<StringType, SerializableType>()

        while (ctx.state.native.tableNext(arg1.index)) {
            val k = ctx.getStringValue(-2)
            val v = ctx.getAny(-1)
            when (val n = v.value) {
                is DoubleType -> eventMap[k] = n
                is StringType -> eventMap[k] = n
                else -> TODO()
            }
            ctx.native.pop(1)
        }


        // FIXME make sure topic is set and string
        require(eventMap.containsKey(StringType("topic")))

        eventsCollector.add(Event(TableType(eventMap)))
    }
}
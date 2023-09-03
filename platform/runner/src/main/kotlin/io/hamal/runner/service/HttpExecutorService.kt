package io.hamal.runner.service

import io.hamal.lib.http.HttpTemplate
import io.hamal.lib.sdk.DefaultHubSdk
import io.hamal.runner.config.SandboxFactory
import io.hamal.runner.connector.HttpConnector
import io.hamal.runner.connector.UnitOfWork
import io.hamal.runner.run.DefaultCodeRunner
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.toJavaDuration

@Service
class HttpExecutorService(
    private val httpTemplate: HttpTemplate,
    private val runnerExecutor: ThreadPoolTaskScheduler,
    private val sandboxFactory: SandboxFactory
) {
    @Scheduled(initialDelay = 1, timeUnit = TimeUnit.SECONDS, fixedRate = Int.MAX_VALUE.toLong())
    fun run() {
        val sdk = DefaultHubSdk(httpTemplate)
        val connector = HttpConnector(sdk)
        val execute = DefaultCodeRunner(connector, sandboxFactory)
        runnerExecutor.scheduleAtFixedRate({
            connector.poll().forEach { uow ->

                execute(
                    UnitOfWork(
                        id = uow.id,
                        inputs = uow.inputs,
                        state = uow.state,
                        code = uow.code,
                        correlation = uow.correlation,
                        events = uow.events
                    )
                )

//                    try {
//                        log.debug("Picked up: {}", unitOfWork.id)
//
////                        lateinit var stateResult: State
//                        val eventsCollector = mutableListOf<Event>()
//
//                        sandboxFactory.create(DefaultSandboxContext())
//                            .use { sb ->
//                                val ctxExtension = CtxExtensionFactory(eventsCollector).create()
//
//                                val internalTable = sb.state.tableCreateMap(ctxExtension.internals.size)
//                                ctxExtension.internals.forEach { entry ->
//                                    val fn = entry.value
//                                    require(fn is FunctionType<*, *, *, *>)
//                                    internalTable[entry.key] = fn
//                                }
//
//                                sb.setGlobal("_internal", internalTable)
//                                sb.state.load(ctxExtension.init)
//                                sb.state.load("${ctxExtension.name} = create_extension_factory()()")
//                                sb.unsetGlobal("_internal")
//
////                            sb.run { state ->
////                                val ctx = state.tableCreateMap(1)
//
////                                sb.native.pushFunction(EmitFunction(eventsCollector))
////                                sb.native.tabletSetField(ctx.index, "emit")
//
////                                val funcState = state.tableCreateMap(1)
////                                exec.state.value.forEach { entry ->
////                                    when (val value = entry.value) {
////                                        is BooleanType -> funcState[entry.key] = value
////                                        is NumberType -> funcState[entry.key] = value
////                                        is StringType -> funcState[entry.key] = value
////                                        else -> TODO()
////                                    }
////                                }
////                                ctx["state"] = funcState
////                                log.debug("Injected state into context")
////
////
////                                val invocation = exec.invocation
////                                val events = state.tableCreateArray(0)
////                                if (invocation is EventInvocation) {
////                                    invocation.events.forEach { evt ->
////                                        val evtTable = state.tableCreateMap(1)
////                                        evt.value.forEach { payload ->
////                                            when (val v = payload.value) {
////                                                is StringType -> evtTable[payload.key] = v
////                                                is NumberType -> evtTable[payload.key] = v
////                                                else -> TODO()
////                                            }
////                                        }
////                                        sb.native.tableAppend(events.index)
////                                    }
////                                }
////
////                                ctx["events"] = events
////                                log.debug("Injected {} events into context", events.length())
////                                state.setGlobal("ctx", ctx)
////                            }
//
//                                log.debug("Start code execution")
//                                sb.load(unitOfWork.code)
//                                log.debug("Code execution finished normally")
//
//
////                            sb.run { state ->
////                                val ctx = state.getGlobalTableMap("ctx")
////                                val funcState = ctx.getTableMap("state")
////
////                                val stateMap = mutableMapOf<StringType, SerializableType>()
////
////                                state.native.pushNil()
////                                while (state.native.tableNext(funcState.index)) {
////                                    val k = state.getStringValue(-2)
////                                    val v = state.getAny(-1)
////
////                                    when (val n = v.value) {
////                                        is NumberType -> stateMap[k] = n
////                                        is StringType -> stateMap[k] = n
////                                        else -> TODO()
////                                    }
////
////                                    state.native.pop(1)
////                                }
////
////                                stateResult = State(TableType(stateMap))
////                            }
//                            }
//
//                        sdk.execService().complete(unitOfWork.id, State(), eventsCollector)
//                        log.debug("Completed exec: {}", unitOfWork.id)
//
//                    } catch (kua: ExtensionError) {
//                        kua.printStackTrace()
//
//                        val e = kua.cause
//                        if (e is ExitError) {
//                            if (e.status == NumberType(0.0)) {
//                                sdk.execService().complete(unitOfWork.id, State(), listOf())
//                                log.debug("Completed exec: {}", unitOfWork.id)
//                            } else {
//                                sdk.execService().fail(unitOfWork.id, ErrorType(e.message ?: "Unknown reason"))
//                                log.debug("Failed exec: {}", unitOfWork.id)
//                            }
//                        } else {
//                            sdk.execService().fail(unitOfWork.id, ErrorType(kua.message ?: "Unknown reason"))
//                            log.debug("Failed exec: {}", unitOfWork.id)
//                        }
//                    } catch (a: AssertionError) {
//                        a.printStackTrace()
//                        sdk.execService().fail(unitOfWork.id, ErrorType(a.message ?: "Unknown reason"))
//                        log.debug("Assertion error: {} - {}", unitOfWork.id, a.message)
//                    } catch (t: Throwable) {
//                        t.printStackTrace()
//                        sdk.execService().fail(unitOfWork.id, ErrorType(t.message ?: "Unknown reason"))
//                        log.debug("Failed exec: {}", unitOfWork.id)
//                    }
//                }
            }
        }, 10.milliseconds.toJavaDuration())
    }

    companion object {
        private val log = LoggerFactory.getLogger(HttpExecutorService::class.java)
    }
}


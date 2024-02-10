package io.hamal.runner.run

import io.hamal.lib.common.logger
import io.hamal.lib.domain.vo.*
import io.hamal.lib.kua.AssertionError
import io.hamal.lib.kua.ExitError
import io.hamal.lib.kua.ExtensionError
import io.hamal.lib.kua.table.TableProxyArray
import io.hamal.lib.kua.table.TableProxyMap
import io.hamal.lib.kua.type.*
import io.hamal.runner.config.EnvFactory
import io.hamal.runner.config.SandboxFactory
import io.hamal.runner.connector.Connector
import io.hamal.runner.connector.UnitOfWork

interface CodeRunner {
    fun run(unitOfWork: UnitOfWork)
}

class CodeRunnerImpl(
    private val connector: Connector,
    private val sandboxFactory: SandboxFactory,
    private val envFactory: EnvFactory
) : CodeRunner {

    private lateinit var runnerContext: RunnerContext

    val context get() = runnerContext

    override fun run(unitOfWork: UnitOfWork) {
        val execId = unitOfWork.id
        try {
            log.debug("Start execution: $execId")

            runnerContext = RunnerContext(
                unitOfWork.state,
                unitOfWork.invocation
            )
            runnerContext[ExecId::class] = unitOfWork.id
            runnerContext[NamespaceId::class] = unitOfWork.namespaceId
            runnerContext[GroupId::class] = unitOfWork.groupId
            runnerContext[ExecToken::class] = unitOfWork.token
            runnerContext[RunnerEnv::class] = envFactory.create()

            sandboxFactory.create(runnerContext)
                .use { sandbox ->

                    try {
                        val contextExtension = RunnerContextFactory(runnerContext).create(sandbox)

                        val internalTable = sandbox.state.tableCreateMap(contextExtension.internals.size)
                        contextExtension.internals.forEach { entry ->
                            when (val value = entry.value) {
                                is KuaNil -> {}
                                is KuaString -> internalTable[entry.key] = value
                                is KuaNumber -> internalTable[entry.key] = value
                                is KuaFunction<*, *, *, *> -> internalTable[entry.key] = value
                                is TableProxyArray -> internalTable[entry.key] = value
                                is TableProxyMap -> internalTable[entry.key] = value
                                is KuaMap -> internalTable[entry.key] = sandbox.toProxyMap(value)
                                else -> TODO()
                            }
                        }

                        sandbox.setGlobal("_internal", internalTable)
                        sandbox.state.load(contextExtension.factoryCode)

                        sandbox.state.load("${contextExtension.name} = plugin()()")
                        sandbox.unsetGlobal("_internal")

                        sandbox.load(KuaCode(unitOfWork.code.value))

                        val ctx = sandbox.getGlobalTableMap("context")
                        val stateToSubmit = sandbox.toKuaMap(ctx.getTableMap("state")).toHotObject()

                        connector.complete(execId, ExecResult(), ExecState(stateToSubmit), runnerContext.eventsToSubmit)
                        log.debug("Completed exec: $execId")
                    } catch (e: ExtensionError) {
                        val cause = e.cause
                        if (cause is ExitError) {
                            if (cause.status == KuaNumber(0.0)) {

                                val ctx = sandbox.getGlobalTableMap("context")
                                val stateToSubmit = sandbox.toKuaMap(ctx.getTableMap("state")).toHotObject()

                                connector.complete(
                                    execId,
                                    ExecResult(cause.result.toHotObject()),
                                    ExecState(stateToSubmit),
                                    runnerContext.eventsToSubmit
                                )
                                log.debug("Completed exec: $execId")
                            } else {
                                connector.fail(execId, ExecResult(cause.result.toHotObject()))
                                log.debug("Failed exec: $execId")
                            }

                        } else {
                            e.printStackTrace()
                            connector.fail(
                                execId,
                                ExecResult(
                                    KuaMap(mutableMapOf("message" to KuaString(e.message ?: "Unknown reason")))
                                        .toHotObject()
                                )
                            )
                            log.debug("Failed exec: $execId")
                        }
                    }
                }
        } catch (a: AssertionError) {
            a.printStackTrace()
            connector.fail(
                execId,
                ExecResult(
                    KuaMap(mutableMapOf("message" to KuaString(a.message ?: "Unknown reason")))
                        .toHotObject()
                )
            )
            log.debug("Assertion error: $execId - ${a.message}")
        } catch (t: Throwable) {
            t.printStackTrace()
            connector.fail(
                execId,
                ExecResult(
                    KuaMap(mutableMapOf("message" to KuaString(t.message ?: "Unknown reason")))
                        .toHotObject()
                )
            )
            log.debug("Failed exec: $execId")
        }
    }

    companion object {
        private val log = logger(CodeRunnerImpl::class)
    }
}
package io.hamal.runner.run

import io.hamal.lib.common.hot.HotNumber
import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.common.logger
import io.hamal.lib.domain.vo.*
import io.hamal.lib.kua.AssertionError
import io.hamal.lib.kua.ExitError
import io.hamal.lib.kua.ExtensionError
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
            runnerContext[WorkspaceId::class] = unitOfWork.workspaceId
            runnerContext[NamespaceId::class] = unitOfWork.namespaceId
            runnerContext[ExecToken::class] = unitOfWork.token
            runnerContext[RunnerEnv::class] = envFactory.create()

            sandboxFactory.create(runnerContext)
                .use { sandbox ->

                    try {
                        val contextExtension = RunnerContextFactory(runnerContext).create(sandbox)

                        val internalTable = sandbox.tableCreate(0, contextExtension.internals.size)
                        contextExtension.internals.forEach { entry ->
                            when (val value = entry.value) {
                                is KuaNil -> {}
                                is KuaString -> internalTable[entry.key] = value
                                is KuaNumber -> internalTable[entry.key] = value
                                is KuaFunction<*, *, *, *> -> internalTable[entry.key] = value
                                is KuaTable -> internalTable[entry.key] = value
                                is KuaTable -> internalTable[entry.key] = value
//                                is KuaTable -> internalTable[entry.key] = sandbox.toKuaTableMap(value)
//                                is KuaTable -> internalTable[entry.key] = sandbox.toTableArray(value)
                                else -> TODO()
                            }
                        }

                        sandbox.globalSet(KuaString("_internal"), internalTable)
                        sandbox.codeLoad(contextExtension.factoryCode)

                        sandbox.codeLoad(KuaCode("${contextExtension.name} = plugin_factory_create()()"))
                        sandbox.globalUnset(KuaString("_internal"))

                        sandbox.codeLoad(KuaCode(unitOfWork.code.value))

                        val ctx = sandbox.globalGetTable(KuaString("context"))
                        val stateToSubmit = ctx.getTableMap("state").toHotObject()

                        connector.complete(execId, ExecResult(), ExecState(stateToSubmit), runnerContext.eventsToSubmit)
                        log.debug("Completed exec: $execId")
                    } catch (e: ExtensionError) {
                        val cause = e.cause
                        if (cause is ExitError) {
                            if (cause.status == HotNumber(0.0)) {

                                val ctx = sandbox.globalGetTable(KuaString("context"))
                                val stateToSubmit = ctx.getTableMap("state").toHotObject()

                                connector.complete(
                                    execId,
                                    ExecResult(cause.result),
                                    ExecState(stateToSubmit),
                                    runnerContext.eventsToSubmit
                                )
                                log.debug("Completed exec: $execId")
                            } else {
                                connector.fail(execId, ExecResult(cause.result))
                                log.debug("Failed exec: $execId")
                            }

                        } else {
                            e.printStackTrace()
                            connector.fail(
                                execId,
                                ExecResult(HotObject.builder().set("message", e.message ?: "Unknown reason").build())
                            )
                            log.debug("Failed exec: $execId")
                        }
                    }
                }
        } catch (a: AssertionError) {
            a.printStackTrace()
            connector.fail(
                execId,
                ExecResult(HotObject.builder().set("message", a.message ?: "Unknown reason").build())
            )
            log.debug("Assertion error: $execId - ${a.message}")
        } catch (t: Throwable) {
            t.printStackTrace()
            connector.fail(
                execId,
                ExecResult(HotObject.builder().set("message", t.message ?: "Unknown reason").build())
            )
            log.debug("Failed exec: $execId")
        }
    }

    companion object {
        private val log = logger(CodeRunnerImpl::class)
    }
}
package io.hamal.runner.run

import io.hamal.lib.common.logger
import io.hamal.lib.common.serialization.json.JsonNumber
import io.hamal.lib.common.value.*
import io.hamal.lib.domain._enum.CodeType
import io.hamal.lib.domain.vo.*
import io.hamal.lib.kua.AssertionError
import io.hamal.lib.kua.ExitError
import io.hamal.lib.kua.ExtensionError
import io.hamal.lib.kua.tableCreate
import io.hamal.lib.kua.value.KuaFunction
import io.hamal.lib.kua.value.KuaTable
import io.hamal.lib.kua.value.toValueObject
import io.hamal.lib.nodes.NodesGraph
import io.hamal.lib.nodes.compiler.graph.GraphCompiler
import io.hamal.lib.nodes.serde
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
                unitOfWork.inputs
            )
            runnerContext[ExecId::class] = unitOfWork.id
            runnerContext[WorkspaceId::class] = unitOfWork.workspaceId
            runnerContext[NamespaceId::class] = unitOfWork.namespaceId
            runnerContext[ExecToken::class] = unitOfWork.execToken
            runnerContext[RunnerEnv::class] = envFactory.create()

            sandboxFactory.create(runnerContext)
                .use { sandbox ->

                    try {
                        val contextExtension = RunnerContextFactory(runnerContext).create(sandbox)

                        val internalTable = sandbox.tableCreate(0, contextExtension.internals.size)
                        contextExtension.internals.forEach { entry ->
                            when (val value = entry.value) {
                                is ValueNil -> {}
                                is ValueString -> internalTable[entry.key] = value
                                is ValueNumber -> internalTable[entry.key] = value
                                is KuaFunction<*, *, *, *> -> internalTable[entry.key] = value
                                is KuaTable -> internalTable[entry.key] = value
                                else -> TODO()
                            }
                        }
                        sandbox.globalSet(ValueString("_internal"), internalTable)
                        sandbox.codeLoad(contextExtension.factoryCode)
//
                        sandbox.codeLoad(ValueCode("${contextExtension.name} = plugin_create(_internal)"))
                        sandbox.globalUnset(ValueString("_internal"))

                        when (unitOfWork.codeType) {
                            CodeType.None -> TODO()
                            CodeType.Lua54 -> {
                                sandbox.codeLoad(unitOfWork.code)
                            }

                            CodeType.Nodes -> {
                                // FIXME load graph from code
                                val graph = serde.read(NodesGraph::class, unitOfWork.code.stringValue)
                                val compiledCode = GraphCompiler(sandbox.generatorNodeCompilerRegistry).compile(graph)
                                sandbox.codeLoad(compiledCode)
                            }
                        }


                        val ctx = sandbox.globalGetTable(ValueString("context"))

                        // FIXME nodes can have state as well
                        val stateToSubmit = if (unitOfWork.codeType == CodeType.Lua54) {
                            ctx.getTable(ValueString("state")).toValueObject()
                        } else {
                            ctx.getTable(ValueString("state")).toValueObject()
                        }

                        connector.complete(
                            execId,
                            ExecResult(),
                            ExecState(stateToSubmit),
                            runnerContext.eventsToSubmit
                        )
                        log.debug("Completed exec: $execId")
                    } catch (e: ExtensionError) {
                        val cause = e.cause
                        if (cause is ExitError) {
                            if (cause.status == JsonNumber(0.0)) {

                                val ctx = sandbox.globalGetTable(ValueString("context"))
                                val stateToSubmit = ctx.getTable(ValueString("state")).toValueObject()

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
                                ExecResult(ValueObject.builder().set("message", e.message ?: "Unknown reason").build()) // FIXME use ValueError
                            )
                            log.debug("Failed exec: $execId")
                        }
                    }
                }
        } catch (a: AssertionError) {
            a.printStackTrace()
            connector.fail(
                execId,
                ExecResult(ValueObject.builder().set("message", a.message ?: "Unknown reason").build())
            )
            log.debug("Assertion error: $execId - ${a.message}")
        } catch (t: Throwable) {
            t.printStackTrace()
            connector.fail(
                execId,
                ExecResult(ValueObject.builder().set("message", t.message ?: "Unknown reason").build())
            )
            log.debug("Failed exec: $execId")
        }
    }

    companion object {
        private val log = logger(CodeRunnerImpl::class)
    }
}
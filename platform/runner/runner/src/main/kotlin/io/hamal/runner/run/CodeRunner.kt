package io.hamal.runner.run

import io.hamal.lib.common.logger
import io.hamal.lib.common.value.*
import io.hamal.lib.domain._enum.CodeTypes.*
import io.hamal.lib.domain.vo.*
import io.hamal.lib.domain.vo.CodeType.Companion.CodeType
import io.hamal.lib.domain.vo.ExecStatusCode.Companion.ExecStatusCode
import io.hamal.lib.kua.*
import io.hamal.lib.kua.value.KuaFunction
import io.hamal.lib.kua.value.KuaTable
import io.hamal.lib.kua.value.toValueObject
import io.hamal.lib.nodes.NodesGraph
import io.hamal.lib.nodes.compiler.graph.GraphCompilerImpl
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

        log.debug("Start execution: $execId")

        runnerContext = RunnerContext(unitOfWork.state, unitOfWork.inputs)
        runnerContext[ExecId::class] = unitOfWork.id
        runnerContext[WorkspaceId::class] = unitOfWork.workspaceId
        runnerContext[NamespaceId::class] = unitOfWork.namespaceId
        runnerContext[TriggerId::class] = unitOfWork.triggerId
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

                    sandbox.codeLoad(ValueCode("${contextExtension.name} = plugin_create(_internal)"))
                    sandbox.globalUnset(ValueString("_internal"))

                    when (unitOfWork.codeType) {
                        CodeType(None) -> TODO()
                        CodeType(Lua54) -> {
                            sandbox.codeLoad(unitOfWork.code.value)
                        }

                        CodeType(Nodes) -> {
                            // FIXME load graph from code
                            val graph = serde.read(NodesGraph::class, unitOfWork.code.stringValue)
                            val compiledCode = GraphCompilerImpl(sandbox.generatorNodeCompilerRegistry).compile(graph)
                            sandbox.codeLoad(compiledCode)
                        }
                    }

                    sandbox.codeLoad(ValueCode("""context.complete({})"""))

                } catch (e: ErrorPlugin) {
                    val cause = e.cause
                    when (cause) {
                        is ExitComplete -> {

                            val ctx = sandbox.globalGetTable(ValueString("context"))
                            val stateToSubmit = ctx.getTable(ValueString("state")).toValueObject()

                            connector.complete(
                                execId,
                                ExecStatusCode(cause.statusCode),
                                ExecResult(cause.result),
                                ExecState(stateToSubmit),
                                runnerContext.eventsToSubmit
                            )
                            log.debug("Completed exec: $execId")

                        }

                        is ExitFailure -> {
                            e.printStackTrace()
                            connector.fail(
                                execId,
                                ExecStatusCode(cause.statusCode),
                                ExecResult(cause.result)
                            )
                            log.debug("Failed exec: $execId")
                        }

                        else -> {
                            e.printStackTrace()
                            connector.fail(
                                execId,
                                ExecStatusCode(500),
                                ExecResult(ValueObject.builder().set("message", e.message ?: "Unknown reason").build())
                            )
                        }
                    }
                } catch (e: ErrorAssertion) {
                    e.printStackTrace()
                    connector.fail(
                        execId,
                        ExecStatusCode(500),
                        ExecResult(ValueObject.builder().set("message", e.message ?: "Unknown reason").build())
                    )
                } catch (e: ErrorIllegalArgument) {
                    e.printStackTrace()
                    connector.fail(
                        execId,
                        ExecStatusCode(400),
                        ExecResult(ValueObject.builder().set("message", e.message ?: "Unknown reason").build())
                    )
                } catch (e: ErrorIllegalState) {
                    e.printStackTrace()
                    connector.fail(
                        execId,
                        ExecStatusCode(409),
                        ExecResult(ValueObject.builder().set("message", e.message ?: "Unknown reason").build())
                    )
                } catch (e: ErrorNotFound) {
                    e.printStackTrace()
                    connector.fail(
                        execId,
                        ExecStatusCode(404),
                        ExecResult(ValueObject.builder().set("message", e.message ?: "Unknown reason").build())
                    )
                } catch (t: Throwable) {
                    t.printStackTrace()
                    connector.fail(
                        execId,
                        ExecStatusCode(500),
                        ExecResult(ValueObject.builder().set("message", t.message ?: "Unknown reason").build())
                    )
                }
            }
    }

    companion object {
        private val log = logger(CodeRunnerImpl::class)
    }
}
package io.hamal.runner.run

import io.hamal.lib.common.logger
import io.hamal.lib.domain.State
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.ExecToken
import io.hamal.lib.domain.vo.GroupId
import io.hamal.lib.kua.AssertionError
import io.hamal.lib.kua.ExitError
import io.hamal.lib.kua.ExtensionError
import io.hamal.lib.kua.function.FunctionType
import io.hamal.lib.kua.table.TableProxyArray
import io.hamal.lib.kua.table.TableProxyMap
import io.hamal.lib.kua.type.CodeType
import io.hamal.lib.kua.type.ErrorType
import io.hamal.lib.kua.type.NumberType
import io.hamal.lib.kua.type.StringType
import io.hamal.runner.config.SandboxFactory
import io.hamal.runner.connector.Connector
import io.hamal.runner.connector.UnitOfWork

interface CodeRunner {
    fun run(unitOfWork: UnitOfWork)
}

class CodeRunnerImpl(
    private val connector: Connector,
    private val sandboxFactory: SandboxFactory
) : CodeRunner {

    private lateinit var runnerContext: RunnerContext

    val context get() = runnerContext

    override fun run(unitOfWork: UnitOfWork) {
        val execId = unitOfWork.id
        try {
            log.debug("Start execution: $execId")

            runnerContext = RunnerContext(RunnerInvocationEvents(unitOfWork.events))
            runnerContext[ExecId::class] = unitOfWork.id
            runnerContext[GroupId::class] = unitOfWork.groupId
            runnerContext[ExecToken::class] = unitOfWork.token

            sandboxFactory.create(runnerContext)
                .use { sandbox ->

                    val ctxExtension = RunnerContextFactory(runnerContext).create(sandbox)

                    val internalTable = sandbox.state.tableCreateMap(ctxExtension.internals.size)
                    ctxExtension.internals.forEach { entry ->
                        when (val value = entry.value) {
                            is StringType -> internalTable[entry.key] = value
                            is NumberType -> internalTable[entry.key] = value
                            is FunctionType<*, *, *, *> -> internalTable[entry.key] = value
                            is TableProxyArray -> internalTable[entry.key] = value
                            is TableProxyMap -> internalTable[entry.key] = value

                            else -> TODO()
                        }
                    }


                    sandbox.setGlobal("_internal", internalTable)
                    sandbox.state.load(ctxExtension.init)
                    sandbox.state.load("${ctxExtension.name} = create_extension_factory()()")
                    sandbox.unsetGlobal("_internal")

                    sandbox.load(CodeType(unitOfWork.code.value))
                }

            connector.complete(execId, State(), runnerContext.eventsToSubmit)
            log.debug("Completed exec: $execId")

        } catch (e: ExtensionError) {
            val cause = e.cause
            if (cause is ExitError) {
                if (cause.status == NumberType(0.0)) {
                    connector.complete(execId, State(), listOf())
                    log.debug("Completed exec: $execId")
                } else {
                    connector.fail(execId, ErrorType(e.message ?: "Unknown reason"))
                    log.debug("Failed exec: $execId")
                }
            } else {
                connector.fail(execId, ErrorType(e.message ?: "Unknown reason"))
                log.debug("Failed exec: $execId")
            }
        } catch (a: AssertionError) {
            a.printStackTrace()
            connector.fail(execId, ErrorType(a.message ?: "Unknown reason"))
            log.debug("Assertion error: $execId - ${a.message}")
        } catch (t: Throwable) {
            connector.fail(execId, ErrorType(t.message ?: "Unknown reason"))
            log.debug("Failed exec: $execId")
        }
    }

    companion object {
        private val log = logger(CodeRunnerImpl::class)
    }
}
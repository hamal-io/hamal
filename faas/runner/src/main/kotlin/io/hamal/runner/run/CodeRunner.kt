package io.hamal.runner.run

import io.hamal.lib.domain.Invocation
import io.hamal.lib.domain.State
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.kua.AssertionError
import io.hamal.lib.kua.ExitError
import io.hamal.lib.kua.ExtensionError
import io.hamal.lib.kua.function.FunctionType
import io.hamal.lib.kua.table.TableProxyArray
import io.hamal.lib.kua.table.TableProxyMap
import io.hamal.lib.kua.type.ErrorType
import io.hamal.lib.kua.type.NumberType
import io.hamal.lib.kua.type.StringType
import io.hamal.runner.config.SandboxFactory
import io.hamal.runner.connector.Connector
import io.hamal.runner.connector.UnitOfWork
import logger

interface CodeRunner {
    operator fun invoke(unitOfWork: UnitOfWork)
}

class DefaultCodeRunner(
    private val connector: Connector,
    private val sandboxFactory: SandboxFactory
) : CodeRunner {

    private lateinit var executionContext: RunnerSandboxContext

    val context get() = executionContext

    override fun invoke(unitOfWork: UnitOfWork) {
        val execId = unitOfWork.id
        try {
            log.debug("Start execution: $execId")

            executionContext = RunnerSandboxContext()
            executionContext[ExecId::class] = unitOfWork.id
            executionContext[Invocation::class] = unitOfWork.invocation

            sandboxFactory.create(executionContext)
                .use { sandbox ->

                    val ctxExtension = RunnerContextFactory(executionContext).create(sandbox)

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

                    sandbox.load(unitOfWork.code)
                }

            connector.complete(execId, State(), executionContext.runnerEmittedEvents)
            log.debug("Completed exec: $execId")

        } catch (e: ExtensionError) {
            e.printStackTrace()
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
            t.printStackTrace()
            connector.fail(execId, ErrorType(t.message ?: "Unknown reason"))
            log.debug("Failed exec: $execId")
        }
    }

    companion object {
        private val log = logger(DefaultCodeRunner::class)
    }
}
package io.hamal.runner.test

import io.hamal.lib.common.value.ValueCode
import io.hamal.lib.domain.EventToSubmit
import io.hamal.lib.domain.State
import io.hamal.lib.domain._enum.CodeType
import io.hamal.lib.domain.vo.*
import io.hamal.lib.domain.vo.ExecId.Companion.ExecId
import io.hamal.lib.domain.vo.ExecToken.Companion.ExecToken
import io.hamal.lib.domain.vo.NamespaceId.Companion.NamespaceId
import io.hamal.lib.domain.vo.WorkspaceId.Companion.WorkspaceId
import io.hamal.lib.kua.NativeLoader
import io.hamal.lib.kua.NativeLoader.Preference.Resources
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.SandboxContext
import io.hamal.lib.kua.extend.extension.RunnerExtensionFactory
import io.hamal.lib.kua.extend.plugin.RunnerPluginFactory
import io.hamal.runner.config.EnvFactory
import io.hamal.runner.config.SandboxFactory
import io.hamal.runner.connector.Connector
import io.hamal.runner.connector.UnitOfWork
import io.hamal.runner.run.CodeRunnerImpl
import org.junit.jupiter.api.fail

class TestConnector(
    val block: (ExecId, ExecStatusCode, ExecResult, ExecState, List<EventToSubmit>) -> Unit = { _, _, _, _, _ -> }
) : Connector {
    override fun poll(): List<UnitOfWork> {
        TODO()
    }

    override fun complete(
        execId: ExecId,
        statusCode: ExecStatusCode,
        result: ExecResult,
        state: ExecState,
        events: List<EventToSubmit>
    ) {
        block(execId, statusCode, result, state, events)
    }

    override fun fail(
        execId: ExecId,
        statusCode: ExecStatusCode,
        result: ExecResult
    ) {
        fail { result.toString() }
    }
}

class TestFailConnector(
    val block: (execId: ExecId, ExecStatusCode, ExecResult) -> Unit = { _, _, _ -> }
) : Connector {
    override fun poll(): List<UnitOfWork> {
        TODO()
    }

    override fun complete(
        execId: ExecId,
        statusCode: ExecStatusCode,
        result: ExecResult,
        state: ExecState,
        events: List<EventToSubmit>
    ) {
        fail { "Test expected to fail" }
    }

    override fun fail(
        execId: ExecId,
        statusCode: ExecStatusCode,
        result: ExecResult
    ) {
        block(execId, statusCode, result)
    }
}


abstract class AbstractRunnerTest {

    fun createTestRunner(
        pluginFactories: List<RunnerPluginFactory> = listOf(),
        extensionFactories: List<RunnerExtensionFactory> = listOf(),
        connector: Connector = TestConnector(),
        env: RunnerEnv = RunnerEnv()
    ) = CodeRunnerImpl(
        connector,
        object : SandboxFactory {
            override fun create(ctx: SandboxContext): Sandbox {
                NativeLoader.load(Resources)
                return Sandbox(ctx)
                    .registerPlugins(*pluginFactories.toTypedArray())
                    .registerExtensions(*extensionFactories.toTypedArray())
            }
        },
        object : EnvFactory {
            override fun create() = env
        }
    )

    fun unitOfWork(
        code: String,
        inputs: ExecInputs = ExecInputs(),
    ) = UnitOfWork(
        id = ExecId(1234),
        execToken = ExecToken("ExecToken"),
        namespaceId = NamespaceId(98876),
        workspaceId = WorkspaceId(5432),
        inputs = inputs,
        state = State(),
        code = ValueCode(code),
        codeType = CodeType.Lua54,
        correlation = null
    )
}
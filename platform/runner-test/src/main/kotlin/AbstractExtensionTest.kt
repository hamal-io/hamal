import io.hamal.lib.domain.State
import io.hamal.lib.domain.vo.*
import io.hamal.lib.kua.NativeLoader
import io.hamal.lib.kua.NativeLoader.Preference.Resources
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.SandboxContext
import io.hamal.lib.kua.extension.plugin.RunnerPluginExtensionFactory
import io.hamal.lib.kua.extension.script.RunnerScriptExtensionFactory
import io.hamal.runner.config.SandboxFactory
import io.hamal.runner.connector.Connector
import io.hamal.runner.connector.UnitOfWork
import io.hamal.runner.run.CodeRunnerImpl
import org.junit.jupiter.api.fail

class TestConnector(
    val block: (ExecId, ExecResult, ExecState, List<EventToSubmit>) -> Unit = { _, _, _, _ -> }
) : Connector {
    override fun poll(): List<UnitOfWork> {
        TODO()
    }

    override fun complete(execId: ExecId, result: ExecResult, state: ExecState, events: List<EventToSubmit>) {
        block(execId, result, state, events)
    }

    override fun fail(execId: ExecId, result: ExecResult) {
        fail { result.value["message"].toString() }
    }
}

class TestFailConnector(
    val block: (execId: ExecId, ExecResult) -> Unit = { _, _ -> }
) : Connector {
    override fun poll(): List<UnitOfWork> {
        TODO()
    }

    override fun complete(execId: ExecId, result: ExecResult, state: ExecState, events: List<EventToSubmit>) {
        fail { "Test expected to fail" }
    }

    override fun fail(execId: ExecId, result: ExecResult) {
        block(execId, result)
    }
}


abstract class AbstractExtensionTest {
    fun createTestRunner(
        pluginFactories: List<RunnerPluginExtensionFactory> = listOf(),
        scriptFactories: List<RunnerScriptExtensionFactory> = listOf(),
        connector: Connector = TestConnector()
    ) = CodeRunnerImpl(
        connector, object : SandboxFactory {
            override fun create(ctx: SandboxContext): Sandbox {
                NativeLoader.load(Resources)
                return Sandbox(ctx)
                    .register(*scriptFactories.toTypedArray())
                    .register(*pluginFactories.toTypedArray())
            }
        }
    )

    fun unitOfWork(
        code: String,
        inputs: ExecInputs = ExecInputs()
    ) = UnitOfWork(
        id = ExecId(1234),
        namespaceId = NamespaceId(98876),
        groupId = GroupId(5432),
        inputs = inputs,
        state = State(),
        code = CodeValue(code),
        correlation = null,
        apiHost = ApiHost("http://test-host")
    )
}
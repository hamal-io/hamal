import io.hamal.lib.domain.EventToSubmit
import io.hamal.lib.domain.State
import io.hamal.lib.domain.vo.*
import io.hamal.lib.kua.NativeLoader
import io.hamal.lib.kua.NativeLoader.Preference.Resources
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.SandboxContext
import io.hamal.lib.kua.extension.ExtensionFactory
import io.hamal.runner.config.SandboxFactory
import io.hamal.runner.connector.Connector
import io.hamal.runner.connector.UnitOfWork
import io.hamal.runner.run.CodeRunnerImpl
import org.junit.jupiter.api.fail

class TestConnector : Connector {
    override fun poll(): List<UnitOfWork> {
        TODO()
    }

    override fun complete(execId: ExecId, result: ExecResult, state: State, events: List<EventToSubmit>) {}
    override fun fail(execId: ExecId, result: ExecResult) {
        fail { result.value["message"].toString() }
    }
}

class TestFailConnector(
    val block: (ExecResult) -> Unit = {}
) : Connector {
    override fun poll(): List<UnitOfWork> {
        TODO()
    }

    override fun complete(execId: ExecId, result: ExecResult, state: State, events: List<EventToSubmit>) {
        fail { "Test expected to fail" }
    }

    override fun fail(execId: ExecId, result: ExecResult) {
        block(result)
    }
}


abstract class AbstractExtensionTest {
    fun createTestExecutor(
        testInstanceFactory: ExtensionFactory<*>,
        connector: Connector = TestConnector()
    ) = CodeRunnerImpl(
        connector, object : SandboxFactory {
            override fun create(ctx: SandboxContext): Sandbox {
                NativeLoader.load(Resources)
                return Sandbox(ctx).register(
                    testInstanceFactory
                )
            }
        }
    )

    fun unitOfWork(
        code: String,
        inputs: ExecInputs = ExecInputs()
    ) = UnitOfWork(
        id = ExecId(1234),
        groupId = GroupId(5432),
        inputs = inputs,
        state = State(),
        code = CodeValue(code),
        correlation = null
    )
}
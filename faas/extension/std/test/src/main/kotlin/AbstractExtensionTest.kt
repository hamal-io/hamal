import io.hamal.lib.domain.EventToSubmit
import io.hamal.lib.domain.State
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.ExecInputs
import io.hamal.lib.kua.NativeLoader
import io.hamal.lib.kua.NativeLoader.Preference.Resources
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.SandboxContext
import io.hamal.lib.kua.extension.ExtensionFactory
import io.hamal.lib.kua.type.CodeType
import io.hamal.lib.kua.type.ErrorType
import io.hamal.runner.config.SandboxFactory
import io.hamal.runner.connector.Connector
import io.hamal.runner.connector.UnitOfWork
import io.hamal.runner.run.DefaultCodeRunner

class TestConnector : Connector {
    override fun poll(): List<UnitOfWork> {
        TODO()
    }

    override fun complete(execId: ExecId, state: State, events: List<EventToSubmit>) {}
    override fun fail(execId: ExecId, error: ErrorType) {
        org.junit.jupiter.api.fail { error.message }
    }
}

class TestFailConnector(
    val block: (ErrorType) -> Unit = {}
) : Connector {
    override fun poll(): List<UnitOfWork> {
        TODO()
    }

    override fun complete(execId: ExecId, state: State, events: List<EventToSubmit>) {
        org.junit.jupiter.api.fail { "Test expected to fail" }
    }

    override fun fail(execId: ExecId, error: ErrorType) {
        block(error)
    }
}


abstract class AbstractExtensionTest {
    fun createTestExecutor(
        testInstanceFactory: ExtensionFactory<*>,
        connector: Connector = TestConnector()
    ) = DefaultCodeRunner(
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
        inputs = ExecInputs(),
        state = State(),
        code = CodeType(code),
        correlation = null
    )
}
package io.hamal.extension.std.algo

import io.hamal.lib.kua.function.Function0In0Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.runner.connector.Connector
import io.hamal.runner.connector.UnitOfWork
import io.hamal.runner.test.RunnerFixture.createTestRunner
import io.hamal.runner.test.TestConnector

internal abstract class AbstractTest {

    fun runTest(unitOfWork: UnitOfWork, connector: Connector = TestConnector()) {
        createTestRunner(
            connector = connector,
            extensionFactories = listOf(ExtensionStdAlgoFactory)
        ).run(unitOfWork)
    }

    data object InvokeFunction : Function0In0Out() {
        override fun invoke(ctx: FunctionContext) {
            invocations++
        }

        var invocations: Int = 0
    }
}
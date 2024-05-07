package io.hamal.extension.std.table

import io.hamal.runner.connector.Connector
import io.hamal.runner.connector.UnitOfWork
import io.hamal.runner.test.RunnerFixture.createTestRunner
import io.hamal.runner.test.TestConnector

internal abstract class AbstractTest {

    fun runTest(unitOfWork: UnitOfWork, connector: Connector = TestConnector()) {
        createTestRunner(
            connector = connector,
            extensionFactories = listOf(ExtensionStdTableFactory)
        ).run(unitOfWork)
    }

}
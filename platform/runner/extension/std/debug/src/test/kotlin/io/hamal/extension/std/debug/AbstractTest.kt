package io.hamal.extension.std.debug

import io.hamal.plugin.std.debug.PluginDebugFactory
import io.hamal.runner.connector.Connector
import io.hamal.runner.connector.UnitOfWork
import io.hamal.runner.test.RunnerFixture.createTestRunner
import io.hamal.runner.test.TestConnector

internal abstract class AbstractTest {

    fun runTest(unitOfWork: UnitOfWork, connector: Connector = TestConnector()) {
        createTestRunner(
            connector = connector,
            extensionFactories = listOf(ExtensionStdDebugFactory),
            pluginFactories = listOf(PluginDebugFactory())
        ).run(unitOfWork)
    }

}
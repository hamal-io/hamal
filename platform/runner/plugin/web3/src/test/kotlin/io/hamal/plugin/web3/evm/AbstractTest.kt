package io.hamal.plugin.web3.evm

import io.hamal.plugin.web3.evm.evm.PluginWeb3EvmFactory
import io.hamal.runner.connector.Connector
import io.hamal.runner.connector.UnitOfWork
import io.hamal.runner.test.RunnerFixture.createTestRunner
import io.hamal.runner.test.TestConnector

internal abstract class AbstractTest {

    fun runTest(unitOfWork: UnitOfWork, connector: Connector = TestConnector()) {
        createTestRunner(
            connector = connector,
            pluginFactories = listOf(PluginWeb3EvmFactory())
        ).run(unitOfWork)
    }

}
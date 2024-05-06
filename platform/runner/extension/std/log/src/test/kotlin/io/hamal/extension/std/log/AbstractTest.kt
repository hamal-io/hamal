package io.hamal.extension.std.log

import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.sdk.api.ApiExecLogAppendRequest
import io.hamal.lib.sdk.api.ApiExecLogService
import io.hamal.plugin.std.log.PluginLogFactory
import io.hamal.runner.connector.Connector
import io.hamal.runner.connector.UnitOfWork
import io.hamal.runner.test.RunnerFixture.createTestRunner
import io.hamal.runner.test.TestConnector

internal abstract class AbstractTest {

    fun runTest(unitOfWork: UnitOfWork, connector: Connector = TestConnector()) {
        createTestRunner(
            connector = connector,
            extensionFactories = listOf(ExtensionStdLogFactory),
            pluginFactories = listOf(PluginLogFactory(testService))
        ).run(unitOfWork)
    }

    val testService = TestExecLogService()

    class TestExecLogService : ApiExecLogService {
        override fun append(execId: ExecId, req: ApiExecLogAppendRequest) {
            this.execId = execId
            this.req = req
        }

        lateinit var execId: ExecId
        lateinit var req: ApiExecLogAppendRequest
    }
}
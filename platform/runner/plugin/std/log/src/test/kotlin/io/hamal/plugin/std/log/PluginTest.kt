package io.hamal.plugin.std.log

import io.hamal.lib.domain._enum.ExecLogLevels.*
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.ExecId.Companion.ExecId
import io.hamal.lib.domain.vo.ExecLogMessage.Companion.ExecLogMessage
import io.hamal.lib.sdk.api.ApiExecLogAppendRequest
import io.hamal.lib.sdk.api.ApiExecLogService
import io.hamal.runner.test.AbstractRunnerTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class PluginLogTest : AbstractRunnerTest() {

    @Test
    fun `Log Trace`() {
        val testService = TestExecLogService()
        val runner = createTestRunner(pluginFactories = listOf(PluginLogFactory(testService)))
        runner.run(unitOfWork("local plugin = require_plugin('std.log'); plugin.log('Trace','a trace message')"))

        assertThat(testService.execId, equalTo(ExecId(1234)))
        with(testService.req) {
            assertThat(level, equalTo(Trace))
            assertThat(message, equalTo(ExecLogMessage("a trace message")))
        }
    }

    @Test
    fun `Log Debug`() {
        val testService = TestExecLogService()
        val runner = createTestRunner(pluginFactories = listOf(PluginLogFactory(testService)))
        runner.run(unitOfWork("local plugin = require_plugin('std.log'); plugin.log('Debug','a debug message')"))

        assertThat(testService.execId, equalTo(ExecId(1234)))
        with(testService.req) {
            assertThat(level, equalTo(Debug))
            assertThat(message, equalTo(ExecLogMessage("a debug message")))
        }
    }

    @Test
    fun `Log Info`() {
        val testService = TestExecLogService()
        val runner = createTestRunner(pluginFactories = listOf(PluginLogFactory(testService)))
        runner.run(unitOfWork("local plugin = require_plugin('std.log'); plugin.log('Info','an info message')"))

        assertThat(testService.execId, equalTo(ExecId(1234)))
        with(testService.req) {
            assertThat(level, equalTo(Info))
            assertThat(message, equalTo(ExecLogMessage("an info message")))
        }
    }

    @Test
    fun `Log Warn`() {
        val testService = TestExecLogService()
        val runner = createTestRunner(pluginFactories = listOf(PluginLogFactory(testService)))
        runner.run(unitOfWork("local plugin = require_plugin('std.log'); plugin.log('Warn','a warning message')"))

        assertThat(testService.execId, equalTo(ExecId(1234)))
        with(testService.req) {
            assertThat(level, equalTo(Warn))
            assertThat(message, equalTo(ExecLogMessage("a warning message")))
        }
    }

    @Test
    fun `Log Error`() {
        val testService = TestExecLogService()
        val runner = createTestRunner(pluginFactories = listOf(PluginLogFactory(testService)))
        runner.run(unitOfWork("local plugin = require_plugin('std.log'); plugin.log('Error','an error message')"))

        assertThat(testService.execId, equalTo(ExecId(1234)))
        with(testService.req) {
            assertThat(level, equalTo(Error))
            assertThat(message, equalTo(ExecLogMessage("an error message")))
        }
    }

    private class TestExecLogService : ApiExecLogService {
        override fun append(execId: ExecId, req: ApiExecLogAppendRequest) {
            this.execId = execId
            this.req = req
        }

        lateinit var execId: ExecId
        lateinit var req: ApiExecLogAppendRequest
    }

}

package io.hamal.plugin.std.log

import AbstractRunnerTest
import io.hamal.lib.domain._enum.ExecLogLevel.*
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.ExecLogMessage
import io.hamal.lib.sdk.api.ApiAppendExecLogCmd
import io.hamal.lib.sdk.api.ApiExecLogService
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class LogTest : AbstractRunnerTest() {

    @Test
    fun `Log Trace`() {
        val testService = TestExecLogService()
        val runner = createTestRunner(pluginFactories = listOf(LogPluginFactory(testService)))
        runner.run(unitOfWork("local log = require('log'); log.trace('a trace message')"))

        assertThat(testService.execId, equalTo(ExecId(1234)))
        with(testService.req) {
            assertThat(level, equalTo(Trace))
            assertThat(message, equalTo(ExecLogMessage("a trace message")))
        }
    }

    @Test
    fun `Log Debug`() {
        val testService = TestExecLogService()
        val runner = createTestRunner(pluginFactories = listOf(LogPluginFactory(testService)))
        runner.run(unitOfWork("local log = require('log'); log.debug('a debug message')"))

        assertThat(testService.execId, equalTo(ExecId(1234)))
        with(testService.req) {
            assertThat(level, equalTo(Debug))
            assertThat(message, equalTo(ExecLogMessage("a debug message")))
        }
    }

    @Test
    fun `Log Info`() {
        val testService = TestExecLogService()
        val runner = createTestRunner(pluginFactories = listOf(LogPluginFactory(testService)))
        runner.run(unitOfWork("local log = require('log'); log.info('an info message')"))

        assertThat(testService.execId, equalTo(ExecId(1234)))
        with(testService.req) {
            assertThat(level, equalTo(Info))
            assertThat(message, equalTo(ExecLogMessage("an info message")))
        }
    }

    @Test
    fun `Log Warn`() {
        val testService = TestExecLogService()
        val runner = createTestRunner(pluginFactories = listOf(LogPluginFactory(testService)))
        runner.run(unitOfWork("local log = require('log'); log.warn('a warning message')"))

        assertThat(testService.execId, equalTo(ExecId(1234)))
        with(testService.req) {
            assertThat(level, equalTo(Warn))
            assertThat(message, equalTo(ExecLogMessage("a warning message")))
        }
    }

    @Test
    fun `Log Error`() {
        val testService = TestExecLogService()
        val runner = createTestRunner(pluginFactories = listOf(LogPluginFactory(testService)))
        runner.run(unitOfWork("local log = require('log'); log.error('an error message')"))

        assertThat(testService.execId, equalTo(ExecId(1234)))
        with(testService.req) {
            assertThat(level, equalTo(Error))
            assertThat(message, equalTo(ExecLogMessage("an error message")))
        }
    }

    @Test
    fun `Log Fatal`() {
        val testService = TestExecLogService()
        val runner = createTestRunner(pluginFactories = listOf(LogPluginFactory(testService)))
        runner.run(unitOfWork("local log = require('log'); log.fatal('a fatal message')"))

        assertThat(testService.execId, equalTo(ExecId(1234)))
        with(testService.req) {
            assertThat(level, equalTo(Fatal))
            assertThat(message, equalTo(ExecLogMessage("a fatal message")))
        }
    }

    private class TestExecLogService : ApiExecLogService {
        override fun append(execId: ExecId, req: ApiAppendExecLogCmd) {
            this.execId = execId
            this.req = req
        }

        lateinit var execId: ExecId
        lateinit var req: ApiAppendExecLogCmd
    }

}

package io.hamal.plugin.std.log

import io.hamal.lib.domain._enum.ExecLogLevels.*
import io.hamal.lib.domain.vo.ExecId.Companion.ExecId
import io.hamal.lib.domain.vo.ExecLogMessage.Companion.ExecLogMessage
import io.hamal.runner.test.RunnerFixture.unitOfWork

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class PluginLogTest : AbstractTest() {

    @Test
    fun `Log Trace`() {
        runTest(
            unitOfWork("local plugin = require_plugin('std.log'); plugin.log('Trace','a trace message')")
        )

        assertThat(testService.execId, equalTo(ExecId(1234)))
        with(testService.req) {
            assertThat(level, equalTo(Trace))
            assertThat(message, equalTo(ExecLogMessage("a trace message")))
        }
    }

    @Test
    fun `Log Debug`() {
        runTest(
            unitOfWork("local plugin = require_plugin('std.log'); plugin.log('Debug','a debug message')")
        )

        assertThat(testService.execId, equalTo(ExecId(1234)))
        with(testService.req) {
            assertThat(level, equalTo(Debug))
            assertThat(message, equalTo(ExecLogMessage("a debug message")))
        }
    }

    @Test
    fun `Log Info`() {
        runTest(
            unitOfWork("local plugin = require_plugin('std.log'); plugin.log('Info','an info message')")
        )

        assertThat(testService.execId, equalTo(ExecId(1234)))
        with(testService.req) {
            assertThat(level, equalTo(Info))
            assertThat(message, equalTo(ExecLogMessage("an info message")))
        }
    }

    @Test
    fun `Log Warn`() {
        runTest(
            unitOfWork("local plugin = require_plugin('std.log'); plugin.log('Warn','a warn message')")
        )

        assertThat(testService.execId, equalTo(ExecId(1234)))
        with(testService.req) {
            assertThat(level, equalTo(Warn))
            assertThat(message, equalTo(ExecLogMessage("a warn message")))
        }
    }

    @Test
    fun `Log Error`() {
        runTest(
            unitOfWork("local plugin = require_plugin('std.log'); plugin.log('Error','an error message')")
        )

        assertThat(testService.execId, equalTo(ExecId(1234)))
        with(testService.req) {
            assertThat(level, equalTo(Error))
            assertThat(message, equalTo(ExecLogMessage("an error message")))
        }
    }


}

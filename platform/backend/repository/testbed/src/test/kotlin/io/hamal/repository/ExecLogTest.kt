package io.hamal.repository

import io.hamal.lib.common.domain.Count.Companion.Count
import io.hamal.lib.common.domain.Limit.Companion.Limit
import io.hamal.lib.common.util.TimeUtils.withInstant
import io.hamal.lib.domain._enum.ExecLogLevels.*
import io.hamal.lib.domain.vo.*
import io.hamal.lib.domain.vo.ExecId.Companion.ExecId
import io.hamal.lib.domain.vo.ExecLogId.Companion.ExecLogId
import io.hamal.lib.domain.vo.ExecLogLevel.Companion.ExecLogLevel
import io.hamal.lib.domain.vo.ExecLogMessage.Companion.ExecLogMessage
import io.hamal.lib.domain.vo.ExecLogTimestamp.Companion.ExecLogTimestamp
import io.hamal.lib.domain.vo.WorkspaceId.Companion.WorkspaceId
import io.hamal.repository.api.ExecLogCmdRepository.AppendCmd
import io.hamal.repository.api.ExecLogQueryRepository.ExecLogQuery
import io.hamal.repository.api.ExecLogRepository
import io.hamal.repository.fixture.AbstractUnitTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestFactory
import java.time.Instant

internal class ExecLogRepositoryTest : AbstractUnitTest() {

    @Nested
    inner class AppendTest {
        @TestFactory
        fun `First time append to exec log`() = runWith(ExecLogRepository::class) {
            withInstant(Instant.ofEpochMilli(23456)) {
                val result = append(
                    AppendCmd(
                        execLogId = ExecLogId(2),
                        execId = ExecId(3),
                        level = ExecLogLevel(Info),
                        message = ExecLogMessage("Some Message"),
                        workspaceId = WorkspaceId(4),
                        timestamp = ExecLogTimestamp(Instant.ofEpochMilli(12345678))
                    )
                )

                with(result) {
                    assertThat(id, equalTo(ExecLogId(2)))
                    assertThat(execId, equalTo(ExecId(3)))
                    assertThat(workspaceId, equalTo(WorkspaceId(4)))
                    assertThat(level, equalTo(Info))
                    assertThat(message, equalTo(ExecLogMessage("Some Message")))
                    assertThat(timestamp, equalTo(ExecLogTimestamp(Instant.ofEpochMilli(12345678))))

                }

                verifyCount(1)
            }
        }

        @TestFactory
        fun `Second time appending to same exec log`() = runWith(ExecLogRepository::class) {
            withInstant(Instant.ofEpochMilli(23456)) {
                appendExecLog(
                    ExecLogId(2),
                    ExecId(3),
                    WorkspaceId(4),
                    ExecLogLevel(Info),
                    ExecLogMessage("First Message")
                )

                val result = append(
                    AppendCmd(
                        execLogId = ExecLogId(2),
                        execId = ExecId(3),
                        level = ExecLogLevel(Info),
                        message = ExecLogMessage("Second Message"),
                        workspaceId = WorkspaceId(4),
                        timestamp = ExecLogTimestamp(Instant.ofEpochMilli(12345678))
                    )
                )

                with(result) {
                    assertThat(id, equalTo(ExecLogId(2)))
                    assertThat(execId, equalTo(ExecId(3)))
                    assertThat(workspaceId, equalTo(WorkspaceId(4)))
                    assertThat(level, equalTo(Info))
                    assertThat(message, equalTo(ExecLogMessage("Second Message")))
                    assertThat(timestamp, equalTo(ExecLogTimestamp(Instant.ofEpochMilli(12345678))))

                }
            }
        }
    }

    @Nested
    inner class ClearTest {

        @TestFactory
        fun `Nothing to clear`() = runWith(ExecLogRepository::class) {
            clear()
            verifyCount(0)
        }

        @TestFactory
        fun `Clear table`() = runWith(ExecLogRepository::class) {
            appendExecLog(ExecLogId(1), ExecId(1), WorkspaceId(3), ExecLogLevel(Info), ExecLogMessage("Here we go"))
            appendExecLog(ExecLogId(3), ExecId(4), WorkspaceId(3), ExecLogLevel(Info), ExecLogMessage("Hamal Rocks"))

            clear()
            verifyCount(0)
        }
    }

    @Nested
    inner class ListAndCountTest {

        @TestFactory
        fun `With workspace ids`() = runWith(ExecLogRepository::class) {
            setup()

            val query = ExecLogQuery(
                workspaceIds = listOf(WorkspaceId(5), WorkspaceId(4)),
                limit = Limit(10)
            )

            assertThat(count(query), equalTo(Count(2)))
            val result = list(query)
            assertThat(result, hasSize(2))

            with(result[0]) {
                assertThat(id, equalTo(ExecLogId(4)))
            }

            with(result[1]) {
                assertThat(id, equalTo(ExecLogId(3)))
                assertThat(workspaceId, equalTo(WorkspaceId(4)))
            }
        }


        @TestFactory
        fun `Limit`() = runWith(ExecLogRepository::class) {
            setup()

            val query = ExecLogQuery(
                workspaceIds = listOf(),
                limit = Limit(3)
            )

            assertThat(count(query), equalTo(Count(4)))
            val result = list(query)
            assertThat(result, hasSize(3))
        }

        @TestFactory
        fun `Skip and limit`() = runWith(ExecLogRepository::class) {
            setup()

            val query = ExecLogQuery(
                afterId = ExecLogId(2),
                workspaceIds = listOf(),
                limit = Limit(1)
            )

            assertThat(count(query), equalTo(Count(1)))
            val result = list(query)
            assertThat(result, hasSize(1))

            with(result[0]) {
                assertThat(id, equalTo(ExecLogId(1)))
            }
        }

        private fun ExecLogRepository.setup() {
            appendExecLog(
                execLogId = ExecLogId(1),
                execId = ExecId(2),
                workspaceId = WorkspaceId(3),
                level = ExecLogLevel(Info),
                message = ExecLogMessage("Message One")
            )

            appendExecLog(
                execLogId = ExecLogId(2),
                execId = ExecId(2),
                workspaceId = WorkspaceId(3),
                level = ExecLogLevel(Warn),
                message = ExecLogMessage("Message Two")
            )

            appendExecLog(
                execLogId = ExecLogId(3),
                execId = ExecId(3),
                workspaceId = WorkspaceId(4),
                level = ExecLogLevel(Error),
                message = ExecLogMessage("Message Three")
            )

            appendExecLog(
                execLogId = ExecLogId(4),
                execId = ExecId(5),
                workspaceId = WorkspaceId(4),
                level = ExecLogLevel(Error),
                message = ExecLogMessage("Message Four")
            )
        }
    }
}

private fun ExecLogRepository.appendExecLog(
    execLogId: ExecLogId,
    execId: ExecId,
    workspaceId: WorkspaceId,
    level: ExecLogLevel,
    message: ExecLogMessage
) {
    append(
        AppendCmd(
            execLogId = execLogId,
            execId = execId,
            workspaceId = workspaceId,
            message = message,
            level = level,
            timestamp = ExecLogTimestamp.now()
        )
    )
}

private fun ExecLogRepository.verifyCount(expected: Int) {
    verifyCount(expected) { }
}

private fun ExecLogRepository.verifyCount(expected: Int, block: ExecLogQuery.() -> Unit) {
    val counted = count(ExecLogQuery(workspaceIds = listOf()).also(block))
    assertThat("number of functions expected", counted, equalTo(Count(expected)))
}
package io.hamal.repository

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.domain.*
import io.hamal.lib.domain.vo.*
import io.hamal.lib.domain.vo.ExecStatus.*
import io.hamal.lib.kua.type.*
import io.hamal.repository.api.Exec
import io.hamal.repository.api.ExecCmdRepository
import io.hamal.repository.api.ExecCmdRepository.*
import io.hamal.repository.api.ExecQueryRepository.ExecQuery
import io.hamal.repository.api.ExecRepository
import io.hamal.repository.fixture.AbstractUnitTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows
import kotlin.math.abs
import kotlin.random.Random

internal class ExecRepositoryTest : AbstractUnitTest() {

    @Nested
    inner class PlanTest {
        @TestFactory
        fun `Plans exec`() = runWith(ExecRepository::class) {
            val result = plan(
                PlanCmd(
                    id = CmdId(1),
                    execId = ExecId(2),
                    groupId = GroupId(3),
                    correlation = Correlation(
                        correlationId = CorrelationId("some-correlation-id"),
                        funcId = FuncId(23)
                    ),
                    inputs = ExecInputs(MapType(mutableMapOf("hamal" to StringType("rocks")))),
                    code = CodeType("40 + 2"),
                    events = listOf(
                        Event(
                            topic = EventTopic(id = TopicId(90), name = TopicName("test-topic")),
                            id = EventId(95),
                            payload = EventPayload(MapType(mutableMapOf("answer" to NumberType(42))))
                        )
                    )
                )
            )

            assertBaseExec(result)
            assertThat(result.status, equalTo(Planned))

            verifyCount(1)
        }

        @TestFactory
        fun `Tires to plan but cmd with exec id was already applied`() = runWith(ExecRepository::class) {
            planExec(
                cmdId = CmdId(23456),
                execId = ExecId(2),
                groupId = GroupId(3)
            )


            val result = planExec(
                cmdId = CmdId(23456),
                execId = ExecId(2),
                groupId = GroupId(4)
            )

            with(result) {
                assertThat(id, equalTo(ExecId(2)))
                assertThat(status, equalTo(Planned))
                assertThat(groupId, equalTo(GroupId(3)))
            }

            verifyCount(1)
        }
    }

    @Nested
    inner class ScheduleTest {

        @TestFactory
        fun `Schedule planned exec`() = runWith(ExecRepository::class) {
            planExec(
                cmdId = CmdId(1),
                execId = ExecId(2),
                groupId = GroupId(3),
            )

            val result = schedule(ScheduleCmd(CmdId(4), ExecId(2)))
            assertBaseExec(result)
            assertThat(result.status, equalTo(Scheduled))

            verifyCount(1)
        }

        @TestFactory
        fun `Tries to schedule exec but not planned`() =
            ExecStatus.values().filter { it != Planned }.flatMap { status ->
                runWith(ExecRepository::class, status.name) {
                    createExec(ExecId(23), status)

                    val exception = assertThrows<IllegalStateException> {
                        schedule(ScheduleCmd(CmdId(4), ExecId(23)))
                    }
                    assertThat(exception.message, equalTo("ExecId(23) not planned"))

                    verifyCount(1)
                }

            }
    }

    @TestFactory
    fun `Tries to schedule exec which does not exists`() =
        runWith(ExecRepository::class) {
            planExec(ExecId(32), GroupId(33))

            val exception = assertThrows<NoSuchElementException> {
                schedule(ScheduleCmd(CmdId(4), ExecId(23)))
            }
            assertThat(exception.message, equalTo("Exec not found"))

            verifyCount(1)
        }

}

private fun assertBaseExec(exec: Exec) {
    assertThat(exec.id, equalTo(ExecId(2)))
    assertThat(exec.groupId, equalTo(GroupId(3)))
    assertThat(
        exec.correlation, equalTo(
            Correlation(
                correlationId = CorrelationId("some-correlation-id"),
                funcId = FuncId(23)
            )
        )
    )
    assertThat(exec.code, equalTo(CodeType("40 + 2")))
    assertThat(
        exec.events, equalTo(
            listOf(
                Event(
                    topic = EventTopic(id = TopicId(90), name = TopicName("test-topic")),
                    id = EventId(95),
                    payload = EventPayload(MapType(mutableMapOf("answer" to NumberType(42))))
                )
            )
        )
    )
}

private fun ExecRepository.planExec(
    execId: ExecId,
    groupId: GroupId,
    cmdId: CmdId = CmdId(abs(Random(10).nextInt()) + 10)
) = plan(
    PlanCmd(
        id = cmdId,
        execId = execId,
        groupId = groupId,
        correlation = Correlation(
            correlationId = CorrelationId("some-correlation-id"),
            funcId = FuncId(23)
        ),
        inputs = ExecInputs(MapType(mutableMapOf("hamal" to StringType("rocks")))),
        code = CodeType("40 + 2"),
        events = listOf(
            Event(
                topic = EventTopic(id = TopicId(90), name = TopicName("test-topic")),
                id = EventId(95),
                payload = EventPayload(MapType(mutableMapOf("answer" to NumberType(42))))
            )
        )
    )
)


private fun ExecRepository.verifyCount(expected: Int) {
    verifyCount(expected) { }
}

private fun ExecRepository.verifyCount(expected: Int, block: ExecQuery.() -> Unit) {
    val counted = count(ExecQuery(groupIds = listOf()).also(block))
    assertThat("number of executions expected", counted, equalTo(expected.toULong()))
}

fun ExecRepository.createExec(
    execId: ExecId,
    status: ExecStatus
): Exec {

    val planedExec = plan(
        PlanCmd(
            id = CmdId(100),
            execId = execId,
            groupId = GroupId(333),
            correlation = null,
            inputs = ExecInputs(),
            code = CodeType(""),
            events = listOf()
        )
    )

    if (status == Planned) {
        return planedExec
    }

    val scheduled = schedule(
        ScheduleCmd(
            id = CmdId(101),
            execId = planedExec.id,
        )
    )

    if (status == Scheduled) {
        return scheduled
    }

    val queued = queue(
        QueueCmd(
            id = CmdId(102),
            execId = scheduled.id
        )
    )
    if (status == Queued) {
        return queued
    }

    val startedExec = start(ExecCmdRepository.StartCmd(CmdId(103))).first()
    if (status == Started) {
        return startedExec
    }

    return when (status) {
        Completed -> complete(
            ExecCmdRepository.CompleteCmd(
                id = CmdId(104),
                execId = startedExec.id
            )
        )

        Failed -> fail(
            ExecCmdRepository.FailCmd(
                id = CmdId(104),
                execId = startedExec.id,
                cause = ErrorType("ExecTest")
            )
        )

        else -> TODO()
    }
}

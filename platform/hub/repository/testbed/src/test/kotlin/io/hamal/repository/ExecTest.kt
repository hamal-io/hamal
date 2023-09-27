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
import org.hamcrest.Matchers.*
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

    @Nested
    inner class QueueTest {

        @TestFactory
        fun `Queue scheduled exec`() = runWith(ExecRepository::class) {
            planExec(
                cmdId = CmdId(1),
                execId = ExecId(2),
                groupId = GroupId(3),
            )

            schedule(ScheduleCmd(CmdId(2), ExecId(2)))

            val result = queue(QueueCmd(CmdId(3), ExecId(2)))
            assertBaseExec(result)
            assertThat(result.status, equalTo(Queued))

            verifyCount(1)
        }

        @TestFactory
        fun `Tries to queue exec but not scheduled`() =
            ExecStatus.values().filter { it != Scheduled }.flatMap { status ->
                runWith(ExecRepository::class, status.name) {
                    createExec(ExecId(23), status)

                    val exception = assertThrows<IllegalStateException> {
                        queue(QueueCmd(CmdId(4), ExecId(23)))
                    }
                    assertThat(exception.message, equalTo("ExecId(23) not scheduled"))

                    verifyCount(1)
                }

            }

        @TestFactory
        fun `Tries to queue exec which does not exists`() =
            runWith(ExecRepository::class) {
                planExec(ExecId(2), GroupId(33))
                schedule(ScheduleCmd(CmdId(2), ExecId(2)))

                val exception = assertThrows<NoSuchElementException> {
                    queue(QueueCmd(CmdId(3), ExecId(23)))
                }
                assertThat(exception.message, equalTo("Exec not found"))

                verifyCount(1)
            }
    }

    @Nested
    inner class StartTest {
        @TestFactory
        fun `Start queued exec`() = runWith(ExecRepository::class) {
            planExec(
                cmdId = CmdId(1),
                execId = ExecId(2),
                groupId = GroupId(3),
            )

            schedule(ScheduleCmd(CmdId(2), ExecId(2)))
            queue(QueueCmd(CmdId(3), ExecId(2)))

            val result = start(StartCmd(CmdId(5)))
            assertThat(result, hasSize(1))

            result.first().let {
                assertBaseExec(it)
                assertThat(it.status, equalTo(Started))
            }

            verifyCount(1)
        }

        @TestFactory
        fun `Nothing to start`() =
            ExecStatus.values().filter { it != Queued }.flatMap { status ->
                runWith(ExecRepository::class, status.name) {
                    createExec(ExecId(23), status)

                    val result = start(StartCmd(CmdId(4)))
                    assertThat(result, empty())

                    verifyCount(1)
                }
            }
    }

    @Nested
    inner class CompleteTest {

        @TestFactory
        fun `Complete started exec`() = runWith(ExecRepository::class) {
            planExec(
                cmdId = CmdId(1),
                execId = ExecId(2),
                groupId = GroupId(3),
            )

            schedule(ScheduleCmd(CmdId(2), ExecId(2)))
            queue(QueueCmd(CmdId(3), ExecId(2)))
            start(StartCmd(CmdId(4)))

            val result = complete(CompleteCmd(CmdId(5), ExecId(2)))
            assertBaseExec(result)
            assertThat(result.status, equalTo(Completed))

            verifyCount(1)
        }

        @TestFactory
        fun `Tries to complete exec but not started`() =
            ExecStatus.values().filter { it != Started }.flatMap { status ->
                runWith(ExecRepository::class, status.name) {
                    createExec(ExecId(23), status)

                    val exception = assertThrows<IllegalStateException> {
                        complete(CompleteCmd(CmdId(4), ExecId(23)))
                    }
                    assertThat(exception.message, equalTo("ExecId(23) not started"))

                    verifyCount(1)
                }

            }

        @TestFactory
        fun `Tries to complete exec which does not exists`() =
            runWith(ExecRepository::class) {
                planExec(ExecId(2), GroupId(33))
                schedule(ScheduleCmd(CmdId(2), ExecId(2)))
                queue(QueueCmd(CmdId(3), ExecId(2)))
                start(StartCmd(CmdId(4)))

                val exception = assertThrows<NoSuchElementException> {
                    complete(CompleteCmd(CmdId(4), ExecId(23)))
                }
                assertThat(exception.message, equalTo("Exec not found"))

                verifyCount(1)
            }
    }

    @Nested
    inner class FailTest {

        @TestFactory
        fun `Fail started exec`() = runWith(ExecRepository::class) {
            planExec(
                cmdId = CmdId(1),
                execId = ExecId(2),
                groupId = GroupId(3),
            )

            schedule(ScheduleCmd(CmdId(2), ExecId(2)))
            queue(QueueCmd(CmdId(3), ExecId(2)))
            start(StartCmd(CmdId(4)))

            val result = fail(FailCmd(CmdId(5), ExecId(2), ErrorType("SomeError")))
            assertBaseExec(result)
            assertThat(result.status, equalTo(Failed))

            verifyCount(1)
        }

        @TestFactory
        fun `Tries to fail exec but not started`() =
            ExecStatus.values().filter { it != Started }.flatMap { status ->
                runWith(ExecRepository::class, status.name) {
                    createExec(ExecId(23), status)

                    val exception = assertThrows<IllegalStateException> {
                        fail(FailCmd(CmdId(4), ExecId(23), ErrorType("SomeError")))
                    }
                    assertThat(exception.message, equalTo("ExecId(23) not started"))

                    verifyCount(1)
                }

            }

        @TestFactory
        fun `Tries to fail exec which does not exists`() =
            runWith(ExecRepository::class) {
                planExec(ExecId(2), GroupId(33))
                schedule(ScheduleCmd(CmdId(2), ExecId(2)))
                queue(QueueCmd(CmdId(3), ExecId(2)))
                start(StartCmd(CmdId(4)))

                val exception = assertThrows<NoSuchElementException> {
                    fail(FailCmd(CmdId(4), ExecId(23), ErrorType("SomeError")))
                }
                assertThat(exception.message, equalTo("Exec not found"))

                verifyCount(1)
            }
    }

    @Nested
    inner class GetTest {
        @TestFactory
        fun `Get exec by id`() = runWith(ExecRepository::class) {
            createExec(ExecId(1), Completed)

            with(get(ExecId(1))) {
                assertThat(id, equalTo(ExecId(1)))
                assertThat(groupId, equalTo(GroupId(333)))
                assertThat(correlation!!.correlationId, equalTo(CorrelationId("SomeCorrelationId")))
                assertThat(correlation!!.funcId, equalTo(FuncId(444)))
                assertThat(inputs, equalTo(ExecInputs(MapType(mutableMapOf("hamal" to StringType("rocks"))))))
                assertThat(code, equalTo(CodeType("'13'..'37'")))
                assertThat(
                    events, equalTo(
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
        }

        @TestFactory
        fun `Tries to get exec by id but does not exist`() = runWith(ExecRepository::class) {
            createExec(ExecId(1), Completed)

            val exception = assertThrows<NoSuchElementException> {
                get(ExecId(111111))
            }
            assertThat(exception.message, equalTo("Exec not found"))
        }
    }

    @Nested
    inner class FindTest {
        @TestFactory
        fun `Find exec by id`() = runWith(ExecRepository::class) {
            createExec(ExecId(1), Completed)

            with(find(ExecId(1))!!) {
                assertThat(id, equalTo(ExecId(1)))
                assertThat(groupId, equalTo(GroupId(333)))
                assertThat(correlation!!.correlationId, equalTo(CorrelationId("SomeCorrelationId")))
                assertThat(correlation!!.funcId, equalTo(FuncId(444)))
                assertThat(inputs, equalTo(ExecInputs(MapType(mutableMapOf("hamal" to StringType("rocks"))))))
                assertThat(code, equalTo(CodeType("'13'..'37'")))
                assertThat(
                    events, equalTo(
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
        }

        @TestFactory
        fun `Tries to find exec by id but does not exist`() = runWith(ExecRepository::class) {
            createExec(ExecId(1), Completed)
            val result = find(ExecId(111111))
            assertThat(result, nullValue())
        }
    }

    @Nested
    inner class ListAndCountTest {

        @TestFactory
        fun `By ids`() = runWith(ExecRepository::class) {
            setup()

            val result = list(listOf(ExecId(111111), ExecId(3)))
            assertThat(result, hasSize(1))

            with(result[0]) {
                assertThat(id, equalTo(ExecId(3)))
                assertThat(groupId, equalTo(GroupId(4)))
            }
        }

        @TestFactory
        fun `With group ids`() = runWith(ExecRepository::class) {
            setup()

            val query = ExecQuery(
                groupIds = listOf(GroupId(5), GroupId(4)),
                limit = io.hamal.lib.common.domain.Limit(10)
            )

            assertThat(count(query), equalTo(2UL))
            val result = list(query)
            assertThat(result, hasSize(2))

            with(result[0]) {
                assertThat(id, equalTo(ExecId(4)))
                assertThat(groupId, equalTo(GroupId(5)))
            }

            with(result[1]) {
                assertThat(id, equalTo(ExecId(3)))
                assertThat(groupId, equalTo(GroupId(4)))
            }
        }


        @TestFactory
        fun `Limit`() = runWith(ExecRepository::class) {
            setup()

            val query = ExecQuery(
                groupIds = listOf(),
                limit = io.hamal.lib.common.domain.Limit(3)
            )

            assertThat(count(query), equalTo(4UL))
            val result = list(query)
            assertThat(result, hasSize(3))
        }

        @TestFactory
        fun `Skip and limit`() = runWith(ExecRepository::class) {
            setup()

            val query = ExecQuery(
                afterId = ExecId(2),
                groupIds = listOf(),
                limit = io.hamal.lib.common.domain.Limit(1)
            )

            assertThat(count(query), equalTo(1UL))
            val result = list(query)
            assertThat(result, hasSize(1))

            with(result[0]) {
                assertThat(id, equalTo(ExecId(1)))
            }
        }

        private fun ExecRepository.setup() {
            createExec(
                execId = ExecId(1),
                groupId = GroupId(3),
                status = Completed
            )

            createExec(
                execId = ExecId(2),
                groupId = GroupId(3),
                status = Failed
            )

            createExec(
                execId = ExecId(3),
                groupId = GroupId(4),
                status = Started
            )

            createExec(
                execId = ExecId(4),
                groupId = GroupId(5),
                status = Queued
            )
        }
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
    status: ExecStatus,
    groupId: GroupId = GroupId(333)
): Exec {

    val planedExec = plan(
        PlanCmd(
            id = CmdId(100),
            execId = execId,
            groupId = groupId,
            correlation = Correlation(
                correlationId = CorrelationId("SomeCorrelationId"),
                funcId = FuncId(444)
            ),
            inputs = ExecInputs(MapType(mutableMapOf("hamal" to StringType("rocks")))),
            code = CodeType("'13'..'37'"),
            events = listOf(
                Event(
                    topic = EventTopic(id = TopicId(90), name = TopicName("test-topic")),
                    id = EventId(95),
                    payload = EventPayload(MapType(mutableMapOf("answer" to NumberType(42))))
                )
            )
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

    val startedExec = start(StartCmd(CmdId(103))).first()
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

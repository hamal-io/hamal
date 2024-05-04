package io.hamal.repository

import io.hamal.lib.common.domain.CmdId
import io.hamal.lib.common.domain.CmdId.Companion.CmdId
import io.hamal.lib.common.domain.Count.Companion.Count
import io.hamal.lib.common.domain.Limit.Companion.Limit
import io.hamal.lib.common.value.ValueCode
import io.hamal.lib.common.value.ValueObject
import io.hamal.lib.domain.Correlation
import io.hamal.lib.domain._enum.ExecStates
import io.hamal.lib.domain._enum.ExecStates.*
import io.hamal.lib.domain.vo.*
import io.hamal.lib.domain.vo.CorrelationId.Companion.CorrelationId
import io.hamal.lib.domain.vo.ExecId.Companion.ExecId
import io.hamal.lib.domain.vo.FuncId.Companion.FuncId
import io.hamal.lib.domain.vo.NamespaceId.Companion.NamespaceId
import io.hamal.lib.domain.vo.TriggerId.Companion.TriggerId
import io.hamal.lib.domain.vo.WorkspaceId.Companion.WorkspaceId
import io.hamal.repository.api.Exec
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
                    triggerId = TriggerId(12),
                    namespaceId = NamespaceId(5),
                    workspaceId = WorkspaceId(3),
                    correlation = Correlation(
                        id = CorrelationId("some-correlation-id"), funcId = FuncId(23)
                    ),
                    inputs = ExecInputs(
                        ValueObject.builder()
                            .set("hamal", "rocks")
                            .build()
                    ),
                    code = ExecCode(value = ValueCode("40 + 2"))
                )
            )

            assertBaseExec(result)
            assertThat(result.status, equalTo(Planned))

            verifyCount(1)
        }

        @TestFactory
        fun `Tires to plan but cmd with exec id was already applied`() = runWith(ExecRepository::class) {
            planExec(
                cmdId = CmdId(23456), execId = ExecId(2), workspaceId = WorkspaceId(3), namespaceId = NamespaceId(4)
            )


            val result = planExec(
                cmdId = CmdId(23456),
                execId = ExecId(2),
                workspaceId = WorkspaceId(4),
                namespaceId = NamespaceId(5),
            )

            with(result) {
                assertThat(id, equalTo(ExecId(2)))
                assertThat(status, equalTo(Planned))
                assertThat(workspaceId, equalTo(WorkspaceId(3)))
                assertThat(namespaceId, equalTo(NamespaceId(4)))
            }

            verifyCount(1)
        }
    }

    @Nested
    inner class ScheduleTest {

        @TestFactory
        fun `Schedule planned exec`() = runWith(ExecRepository::class) {
            planExec(
                cmdId = CmdId(1), execId = ExecId(2), workspaceId = WorkspaceId(3), namespaceId = NamespaceId(4)
            )

            val result = schedule(ScheduleCmd(CmdId(4), ExecId(2)))
            assertBaseExec(result)
            assertThat(result.status, equalTo(Scheduled))

            verifyCount(1)
        }

        @TestFactory
        fun `Tries to schedule exec but not planned`() =
            entries.filter { it != Planned }.flatMap { status ->
                runWith(ExecRepository::class, status.name) {
                    createExec(ExecId(23), status)

                    val exception = assertThrows<IllegalStateException> {
                        schedule(ScheduleCmd(CmdId(4), ExecId(23)))
                    }
                    assertThat(exception.message, equalTo("17 not planned"))

                    verifyCount(1)
                }

            }

        @TestFactory
        fun `Tries to schedule exec which does not exists`() = runWith(ExecRepository::class) {
            planExec(ExecId(32), NamespaceId(34), WorkspaceId(33))

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
                cmdId = CmdId(1), execId = ExecId(2), workspaceId = WorkspaceId(3), namespaceId = NamespaceId(4)
            )

            schedule(ScheduleCmd(CmdId(2), ExecId(2)))

            val result = queue(QueueCmd(CmdId(3), ExecId(2)))
            assertBaseExec(result)
            assertThat(result.status, equalTo(Queued))

            verifyCount(1)
        }

        @TestFactory
        fun `Tries to queue exec but not scheduled`() =
            entries.filter { it != Scheduled }.flatMap { status ->
                runWith(ExecRepository::class, status.name) {
                    createExec(ExecId(23), status)

                    val exception = assertThrows<IllegalStateException> {
                        queue(QueueCmd(CmdId(4), ExecId(23)))
                    }
                    assertThat(exception.message, equalTo("17 not scheduled"))

                    verifyCount(1)
                }

            }

        @TestFactory
        fun `Tries to queue exec which does not exists`() = runWith(ExecRepository::class) {
            planExec(ExecId(2), NamespaceId(44), WorkspaceId(33))
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
                cmdId = CmdId(1), execId = ExecId(2), workspaceId = WorkspaceId(3), namespaceId = NamespaceId(4)
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
        fun `Nothing to start`() = entries.filter { it != Queued }.flatMap { status ->
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
                workspaceId = WorkspaceId(3),
                namespaceId = NamespaceId(4)
            )

            schedule(ScheduleCmd(CmdId(2), ExecId(2)))
            queue(QueueCmd(CmdId(3), ExecId(2)))
            start(StartCmd(CmdId(4)))

            val result = complete(
                CompleteCmd(
                    CmdId(5),
                    ExecId(2),
                    ExecResult(ValueObject.builder().set("answer", 42).build()),
                    ExecState(ValueObject.builder().set("state", 1337).build())
                )
            )

            assertBaseExec(result)
            assertThat(result.status, equalTo(Completed))
            assertThat(result.result, equalTo(ExecResult(ValueObject.builder().set("answer", 42).build())))
            assertThat(result.state, equalTo(ExecState(ValueObject.builder().set("state", 1337).build())))

            verifyCount(1)
        }

        @TestFactory
        fun `Tries to complete exec but not started`() =
            entries.filter { it != Started }.flatMap { status ->
                runWith(ExecRepository::class, status.name) {
                    createExec(ExecId(23), status)

                    val exception = assertThrows<IllegalStateException> {
                        complete(CompleteCmd(CmdId(4), ExecId(23), ExecResult(), ExecState()))
                    }
                    assertThat(exception.message, equalTo("17 not started"))

                    verifyCount(1)
                }

            }

        @TestFactory
        fun `Tries to complete exec which does not exists`() = runWith(ExecRepository::class) {
            planExec(ExecId(2), NamespaceId(44), WorkspaceId(33))
            schedule(ScheduleCmd(CmdId(2), ExecId(2)))
            queue(QueueCmd(CmdId(3), ExecId(2)))
            start(StartCmd(CmdId(4)))

            val exception = assertThrows<NoSuchElementException> {
                complete(CompleteCmd(CmdId(4), ExecId(23), ExecResult(), ExecState()))
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
                cmdId = CmdId(1), execId = ExecId(2), workspaceId = WorkspaceId(3), namespaceId = NamespaceId(4)
            )

            schedule(ScheduleCmd(CmdId(2), ExecId(2)))
            queue(QueueCmd(CmdId(3), ExecId(2)))
            start(StartCmd(CmdId(4)))

            val result = fail(FailCmd(CmdId(5), ExecId(2), ExecResult(ValueObject.builder().set("message", "SomeError").build())))
            assertBaseExec(result)
            assertThat(result.status, equalTo(Failed))
            assertThat(result.result, equalTo(ExecResult(ValueObject.builder().set("message", "SomeError").build())))

            verifyCount(1)
        }

        @TestFactory
        fun `Tries to fail exec but not started`() = entries.filter { it != Started }.flatMap { status ->
            runWith(ExecRepository::class, status.name) {
                createExec(ExecId(23), status)

                val exception = assertThrows<IllegalStateException> {
                    fail(FailCmd(CmdId(4), ExecId(23), ExecResult()))
                }
                assertThat(exception.message, equalTo("17 not started"))

                verifyCount(1)
            }

        }

        @TestFactory
        fun `Tries to fail exec which does not exists`() = runWith(ExecRepository::class) {
            planExec(ExecId(2), NamespaceId(44), WorkspaceId(33))
            schedule(ScheduleCmd(CmdId(2), ExecId(2)))
            queue(QueueCmd(CmdId(3), ExecId(2)))
            start(StartCmd(CmdId(4)))

            val exception = assertThrows<NoSuchElementException> {
                fail(FailCmd(CmdId(4), ExecId(23), ExecResult()))
            }
            assertThat(exception.message, equalTo("Exec not found"))

            verifyCount(1)
        }
    }

    @Nested
    inner class ClearTest {

        @TestFactory
        fun `Nothing to clear`() = runWith(ExecRepository::class) {
            clear()
            verifyCount(0)
        }

        @TestFactory
        fun `Clear table`() = runWith(ExecRepository::class) {
            createExec(ExecId(1), Started, workspaceId = WorkspaceId(3))
            createExec(ExecId(2), Completed, workspaceId = WorkspaceId(345))

            clear()
            verifyCount(0)
        }

    }

    @Nested
    inner class GetTest {
        @TestFactory
        fun `Get exec by id`() = runWith(ExecRepository::class) {
            createExec(
                execId = ExecId(1),
                status = Completed,
                correlation = Correlation(CorrelationId("SomeCorrelationId"), FuncId(444))
            )

            with(get(ExecId(1))) {
                assertThat(id, equalTo(ExecId(1)))
                assertThat(triggerId, equalTo(TriggerId(12)))
                assertThat(workspaceId, equalTo(WorkspaceId(333)))
                assertThat(correlation?.id, equalTo(CorrelationId("SomeCorrelationId")))
                assertThat(correlation?.funcId, equalTo(FuncId(444)))
                assertThat(inputs, equalTo(ExecInputs(ValueObject.builder().set("hamal", "rocks").build())))
                assertThat(code, equalTo(ExecCode(value = ValueCode("'13'..'37'"))))
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
            createExec(
                execId = ExecId(1),
                status = Completed,
                correlation = Correlation(CorrelationId("SomeCorrelationId"), FuncId(444))
            )

            with(find(ExecId(1))!!) {
                assertThat(id, equalTo(ExecId(1)))
                assertThat(triggerId, equalTo(TriggerId(12)))
                assertThat(workspaceId, equalTo(WorkspaceId(333)))
                assertThat(correlation?.id, equalTo(CorrelationId("SomeCorrelationId")))
                assertThat(correlation?.funcId, equalTo(FuncId(444)))
                assertThat(inputs, equalTo(ExecInputs(ValueObject.builder().set("hamal", "rocks").build())))
                assertThat(code, equalTo(ExecCode(value = ValueCode("'13'..'37'"))))
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
                assertThat(workspaceId, equalTo(WorkspaceId(4)))
            }
        }

        @TestFactory
        fun `With ids`() = runWith(ExecRepository::class) {
            setup()

            val query = ExecQuery(
                execIds = listOf(ExecId(1), ExecId(2)),
                limit = Limit(10)
            )

            assertThat(count(query), equalTo(Count(2)))
            val result = list(query)
            assertThat(result, hasSize(2))

            with(result[0]) {
                assertThat(id, equalTo(ExecId(2)))
            }

            with(result[1]) {
                assertThat(id, equalTo(ExecId(1)))
            }
        }


        @TestFactory
        fun `With workspace ids`() = runWith(ExecRepository::class) {
            setup()

            val query = ExecQuery(
                workspaceIds = listOf(WorkspaceId(5), WorkspaceId(4)), limit = Limit(10)
            )

            assertThat(count(query), equalTo(Count(2)))
            val result = list(query)
            assertThat(result, hasSize(2))

            with(result[0]) {
                assertThat(id, equalTo(ExecId(4)))
                assertThat(workspaceId, equalTo(WorkspaceId(5)))
            }

            with(result[1]) {
                assertThat(id, equalTo(ExecId(3)))
                assertThat(workspaceId, equalTo(WorkspaceId(4)))
            }
        }

        @TestFactory
        fun `With func ids`() = runWith(ExecRepository::class) {
            setup()

            val query = ExecQuery(
                funcIds = listOf(FuncId(234), FuncId(123)), workspaceIds = listOf(), limit = Limit(10)
            )

            assertThat(count(query), equalTo(Count(2)))
            val result = list(query)
            assertThat(result, hasSize(2))

            with(result[0]) {
                assertThat(id, equalTo(ExecId(2)))
            }

            with(result[1]) {
                assertThat(id, equalTo(ExecId(1)))
            }
        }

        @TestFactory
        fun `With namespace ids`() = runWith(ExecRepository::class) {
            setup()

            val query = ExecQuery(
                namespaceIds = listOf(NamespaceId(234), NamespaceId(123)), workspaceIds = listOf(), limit = Limit(10)
            )

            assertThat(count(query), equalTo(Count(2)))
            val result = list(query)
            assertThat(result, hasSize(2))

            with(result[0]) {
                assertThat(id, equalTo(ExecId(2)))
            }

            with(result[1]) {
                assertThat(id, equalTo(ExecId(1)))
            }

        }

        @TestFactory
        fun `Limit`() = runWith(ExecRepository::class) {
            setup()

            val query = ExecQuery(
                workspaceIds = listOf(), limit = Limit(3)
            )

            assertThat(count(query), equalTo(Count(4)))
            val result = list(query)
            assertThat(result, hasSize(3))
        }

        @TestFactory
        fun `Skip and limit`() = runWith(ExecRepository::class) {
            setup()

            val query = ExecQuery(
                afterId = ExecId(2), workspaceIds = listOf(), limit = Limit(1)
            )

            assertThat(count(query), equalTo(Count(1)))
            val result = list(query)
            assertThat(result, hasSize(1))

            with(result[0]) {
                assertThat(id, equalTo(ExecId(1)))
            }
        }

        private fun ExecRepository.setup() {
            createExec(
                execId = ExecId(1),
                workspaceId = WorkspaceId(3),
                status = Completed,
                correlation = Correlation(
                    id = CorrelationId("CID-1"),
                    funcId = FuncId(234)
                ),
                namespaceId = NamespaceId(234)
            )

            createExec(
                execId = ExecId(2),
                workspaceId = WorkspaceId(3),
                status = Failed,
                correlation = Correlation(
                    id = CorrelationId("CID-2"),
                    funcId = FuncId(234)
                ),
                namespaceId = NamespaceId(234)
            )

            createExec(
                execId = ExecId(3),
                workspaceId = WorkspaceId(4),
                status = Started,
                correlation = Correlation(
                    id = CorrelationId("CID-1"),
                    funcId = FuncId(444)
                ),
                namespaceId = NamespaceId(444)
            )

            createExec(
                execId = ExecId(4),
                workspaceId = WorkspaceId(5),
                status = Queued,
                correlation = null
            )

        }
    }
}

private fun assertBaseExec(exec: Exec) {
    assertThat(exec.id, equalTo(ExecId(2)))
    assertThat(exec.triggerId, equalTo(TriggerId(12)))
    assertThat(exec.workspaceId, equalTo(WorkspaceId(3)))
    assertThat(
        exec.correlation, equalTo(
            Correlation(
                id = CorrelationId("some-correlation-id"), funcId = FuncId(23)
            )
        )
    )
    assertThat(exec.code, equalTo(ExecCode(value = ValueCode("40 + 2"))))
}

private fun ExecRepository.planExec(
    execId: ExecId,
    namespaceId: NamespaceId,
    workspaceId: WorkspaceId,
    cmdId: CmdId = CmdId(abs(Random(10).nextInt()) + 10)
) = plan(
    PlanCmd(
        id = cmdId,
        execId = execId,
        triggerId = TriggerId(12),
        namespaceId = namespaceId,
        workspaceId = workspaceId,
        correlation = Correlation(
            id = CorrelationId("some-correlation-id"), funcId = FuncId(23)
        ),
        inputs = ExecInputs(ValueObject.builder().set("hamal", "rocks").build()),
        code = ExecCode(value = ValueCode("40 + 2")),
    )
)

private fun ExecRepository.verifyCount(expected: Int) {
    verifyCount(expected) { }
}

private fun ExecRepository.verifyCount(expected: Int, block: ExecQuery.() -> Unit) {
    val counted = count(ExecQuery(workspaceIds = listOf()).also(block))
    assertThat("number of executions expected", counted, equalTo(Count(expected)))
}

fun ExecRepository.createExec(
    execId: ExecId,
    status: ExecStates,
    namespaceId: NamespaceId = NamespaceId(444),
    workspaceId: WorkspaceId = WorkspaceId(333),
    correlation: Correlation? = null
): Exec {

    val planedExec = plan(
        PlanCmd(
            id = CmdId(100),
            execId = execId,
            triggerId = TriggerId(12),
            namespaceId = namespaceId,
            workspaceId = workspaceId,
            correlation = correlation,
            inputs = ExecInputs(ValueObject.builder().set("hamal", "rocks").build()),
            code = ExecCode(value = ValueCode("'13'..'37'")),
        )
    )

    if (status == Planned) {
        return planedExec
    }

    val scheduled = schedule(ScheduleCmd(id = CmdId(101), execId = planedExec.id,))

    if (status == Scheduled) {
        return scheduled
    }

    val queued = queue(QueueCmd(id = CmdId(102), execId = scheduled.id))
    if (status == Queued) {
        return queued
    }

    val startedExec = start(StartCmd(CmdId(103))).first()
    if (status == Started) {
        return startedExec
    }

    return when (status) {
        Completed -> complete(
            CompleteCmd(
                id = CmdId(104),
                execId = startedExec.id,
                result = ExecResult(ValueObject.builder().set("answer", 42).build()),
                state = ExecState(ValueObject.builder().set("state", 1337).build())
            )
        )

        Failed -> fail(
            FailCmd(
                id = CmdId(104),
                execId = startedExec.id,
                result = ExecResult(ValueObject.builder().set("message", "ExecTest").build())
            )
        )

        else -> TODO()
    }
}

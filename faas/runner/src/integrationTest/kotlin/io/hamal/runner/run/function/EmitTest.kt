package io.hamal.runner.run.function

import io.hamal.lib.domain.State
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.ExecInputs
import io.hamal.lib.kua.type.*
import io.hamal.runner.TestFailConnector
import io.hamal.runner.connector.UnitOfWork
import io.hamal.runner.run.AbstractExecuteTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

internal class EmitTest : AbstractExecuteTest() {

    @Test
    fun `No events emitted`() {
        val executor = createTestExecutor()
        executor(unitOfWork(""))

        val eventsToEmit = executor.context.runnerEmittedEvents
        assertThat(eventsToEmit, hasSize(0))
    }

    @Test
    fun `Emit event without topic`() {
        val execute = createTestExecutor(
            connector = TestFailConnector { err ->
                assertThat(err, equalTo(ErrorType("Topic not present")))
            }
        )
        execute(unitOfWork("ctx.emit({value='test-value'})"))

        val eventsToEmit = execute.context.runnerEmittedEvents
        assertThat(eventsToEmit, hasSize(0))
    }


    @Test
    fun `Emit event without payload`() {
        val execute = createTestExecutor()
        execute(unitOfWork("ctx.emit({topic='test-topic'})"))

        val eventsToEmit = execute.context.runnerEmittedEvents
        assertThat(eventsToEmit, hasSize(1))

        with(eventsToEmit.first()) {
            assertThat(value, equalTo(TableType("topic" to StringType("test-topic"))))
        }
    }

    @Test
    fun `Emit event with nil payload`() {
        val execute = createTestExecutor()
        execute(unitOfWork("ctx.emit({topic='test-topic', hamal=nil})"))

        val eventsToEmit = execute.context.runnerEmittedEvents
        assertThat(eventsToEmit, hasSize(1))

        with(eventsToEmit.first()) {
            assertThat(
                value, equalTo(
                    TableType(
                        "topic" to StringType("test-topic")
                    )
                )
            )
        }
    }

    @Test
    fun `Emit event with string payload`() {
        val execute = createTestExecutor()
        execute(unitOfWork("ctx.emit({topic='test-topic', hamal='rocks'})"))

        val eventsToEmit = execute.context.runnerEmittedEvents
        assertThat(eventsToEmit, hasSize(1))

        with(eventsToEmit.first()) {
            assertThat(
                value, equalTo(
                    TableType(
                        "topic" to StringType("test-topic"),
                        "hamal" to StringType("rocks")
                    )
                )
            )
        }
    }

    @Test
    fun `Emit event with number payload`() {
        val execute = createTestExecutor()
        execute(unitOfWork("ctx.emit({topic='test-topic', answer=42})"))

        val eventsToEmit = execute.context.runnerEmittedEvents
        assertThat(eventsToEmit, hasSize(1))

        with(eventsToEmit.first()) {
            assertThat(
                value, equalTo(
                    TableType(
                        "topic" to StringType("test-topic"),
                        "answer" to NumberType(42)
                    )
                )
            )
        }
    }

    @Test
    fun `Emit event with boolean payload`() {
        val execute = createTestExecutor()
        execute(unitOfWork("ctx.emit({topic='test-topic', true_value=true, false_value=false})"))

        val eventsToEmit = execute.context.runnerEmittedEvents
        assertThat(eventsToEmit, hasSize(1))

        with(eventsToEmit.first()) {
            assertThat(
                value, equalTo(
                    TableType(
                        "topic" to StringType("test-topic"),
                        "true_value" to TrueValue,
                        "false_value" to FalseValue
                    )
                )
            )
        }
    }

    @Test
    @Disabled
    fun `Emit event with table payload`() {
        val execute = createTestExecutor()
        execute(unitOfWork("ctx.emit({topic='test-topic', nested_table = { value = 23 }})"))

        val eventsToEmit = execute.context.runnerEmittedEvents
        assertThat(eventsToEmit, hasSize(1))

        with(eventsToEmit.first()) {
            assertThat(
                value, equalTo(
                    TableType(
                        "topic" to StringType("test-topic"),
                        "nested_table" to TableType(
                            "value" to NumberType(23)
                        ),
                    )
                )
            )
        }
    }


    private fun unitOfWork(code: String) = UnitOfWork(
        id = ExecId(1234),
        inputs = ExecInputs(),
        state = State(),
        code = CodeType(code),
        correlation = null
    )
}
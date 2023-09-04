package io.hamal.runner.run.function

import io.hamal.lib.domain.EventPayload
import io.hamal.lib.domain.State
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.ExecInputs
import io.hamal.lib.domain.vo.TopicName
import io.hamal.lib.kua.type.*
import io.hamal.runner.TestFailConnector
import io.hamal.runner.connector.UnitOfWork
import io.hamal.runner.run.AbstractExecuteTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

internal class EmitTest : AbstractExecuteTest() {

    @Test
    fun `No events emitted`() {
        val executor = createTestExecutor()
        executor.run(unitOfWork(""))

        val eventsToEmit = executor.context.eventsToSubmit
        assertThat(eventsToEmit, hasSize(0))
    }

    @Test
    fun `Emit event without topic`() {
        val execute = createTestExecutor(
            connector = TestFailConnector { err ->
                assertThat(err.message, containsString("Topic not present"))
            }
        )
        execute.run(unitOfWork("ctx.emit()"))

        val eventsToEmit = execute.context.eventsToSubmit
        assertThat(eventsToEmit, hasSize(0))
    }


    @Test
    fun `Emit event without payload`() {
        val execute = createTestExecutor()
        execute.run(unitOfWork("ctx.emit('test-topic')"))

        val eventsToEmit = execute.context.eventsToSubmit
        assertThat(eventsToEmit, hasSize(1))

        with(eventsToEmit.first()) {
            assertThat(topicName, equalTo(TopicName("test-topic")))
            assertThat(payload, equalTo(EventPayload()))
        }
    }

    @Test
    fun `Emit event with empty payload`() {
        val execute = createTestExecutor()
        execute.run(unitOfWork("ctx.emit('test-topic', {})"))

        val eventsToEmit = execute.context.eventsToSubmit
        assertThat(eventsToEmit, hasSize(1))

        with(eventsToEmit.first()) {
            assertThat(topicName, equalTo(TopicName("test-topic")))
            assertThat(payload, equalTo(EventPayload()))
        }
    }


    @Test
    fun `Emit event with nil payload`() {
        val execute = createTestExecutor()
        execute.run(unitOfWork("ctx.emit('test-topic', {hamal=nil})"))

        val eventsToEmit = execute.context.eventsToSubmit
        assertThat(eventsToEmit, hasSize(1))

        with(eventsToEmit.first()) {
            assertThat(topicName, equalTo(TopicName("test-topic")))
            assertThat(payload, equalTo(EventPayload()))
        }
    }

    @Test
    fun `Emit event with string payload`() {
        val execute = createTestExecutor()
        execute.run(unitOfWork("ctx.emit('test-topic', {hamal='rocks'})"))

        val eventsToEmit = execute.context.eventsToSubmit
        assertThat(eventsToEmit, hasSize(1))

        with(eventsToEmit.first()) {
            assertThat(topicName, equalTo(TopicName("test-topic")))
            assertThat(payload, equalTo(EventPayload(MapType(mutableMapOf("hamal" to StringType("rocks"))))))
        }
    }

    @Test
    fun `Emit event with number payload`() {
        val execute = createTestExecutor()
        execute.run(unitOfWork("ctx.emit('test-topic',{ answer=42 })"))

        val eventsToEmit = execute.context.eventsToSubmit
        assertThat(eventsToEmit, hasSize(1))

        with(eventsToEmit.first()) {
            assertThat(topicName, equalTo(TopicName("test-topic")))
            assertThat(payload, equalTo(EventPayload(MapType(mutableMapOf("answer" to NumberType(42))))))
        }
    }

    @Test
    fun `Emit event with boolean payload`() {
        val execute = createTestExecutor()
        execute.run(unitOfWork("ctx.emit('test-topic', {true_value=true, false_value=false})"))

        val eventsToEmit = execute.context.eventsToSubmit
        assertThat(eventsToEmit, hasSize(1))

        with(eventsToEmit.first()) {
            assertThat(topicName, equalTo(TopicName("test-topic")))
            assertThat(
                payload, equalTo(
                    EventPayload(
                        MapType(
                            mutableMapOf(
                                "true_value" to TrueValue,
                                "false_value" to FalseValue
                            )
                        )
                    )
                )
            )
        }
    }

    @Test
    @Disabled
    fun `Emit event with table payload`() {
        val execute = createTestExecutor()
        execute.run(unitOfWork("ctx.emit('test-topic', { nested_table = { value = 23 } })"))

        val eventsToEmit = execute.context.eventsToSubmit
        assertThat(eventsToEmit, hasSize(1))

        with(eventsToEmit.first()) {
            assertThat(topicName, equalTo(TopicName("test-topic")))
            assertThat(
                payload, equalTo(
                    EventPayload(
                        MapType(
                            mutableMapOf(
                                "nested_table" to MapType(
                                    mutableMapOf(
                                        "value" to NumberType(23)
                                    ),
                                )
                            )
                        )
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
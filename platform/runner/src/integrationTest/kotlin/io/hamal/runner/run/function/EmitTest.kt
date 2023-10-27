package io.hamal.runner.run.function

import TestConnector
import TestFailConnector
import io.hamal.lib.domain.EventPayload
import io.hamal.lib.domain.State
import io.hamal.lib.domain.vo.*
import io.hamal.lib.kua.type.*
import io.hamal.runner.connector.UnitOfWork
import io.hamal.runner.run.AbstractExecuteTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

internal class EmitTest : AbstractExecuteTest() {

    @Test
    fun `No events emitted`() {
        val runner = createTestRunner(connector = TestConnector { execId, execResult, state, eventToSubmits ->
            assertThat(execId, equalTo(ExecId(1234)))
            assertThat(execResult, equalTo(ExecResult()))
            assertThat(state, equalTo(State()))
            assertThat(eventToSubmits, hasSize(0))
        })
        runner.run(unitOfWork(""))

        val eventsToEmit = runner.context.eventsToSubmit
        assertThat(eventsToEmit, hasSize(0))
    }

    @Test
    fun `Emit event without topic`() {
        val runner = createTestRunner(
            connector = TestFailConnector { execId, result ->
                assertThat(execId, equalTo(ExecId(1234)))
                assertThat(result.value["message"].toString(), containsString("Topic not present"))
            }
        )
        runner.run(unitOfWork("context.emit()"))

        val eventsToEmit = runner.context.eventsToSubmit
        assertThat(eventsToEmit, hasSize(0))
    }


    @Test
    fun `Emit event without payload`() {
        val runner = createTestRunner()
        runner.run(unitOfWork("context.emit('test-topic')"))

        val eventsToEmit = runner.context.eventsToSubmit
        assertThat(eventsToEmit, hasSize(1))

        with(eventsToEmit.first()) {
            assertThat(topicName, equalTo(TopicName("test-topic")))
            assertThat(payload, equalTo(EventPayload()))
        }
    }

    @Test
    fun `Emit event with empty payload`() {
        val runner = createTestRunner()
        runner.run(unitOfWork("context.emit('test-topic', {})"))

        val eventsToEmit = runner.context.eventsToSubmit
        assertThat(eventsToEmit, hasSize(1))

        with(eventsToEmit.first()) {
            assertThat(topicName, equalTo(TopicName("test-topic")))
            assertThat(payload, equalTo(EventPayload()))
        }
    }


    @Test
    fun `Emit event with nil payload`() {
        val runner = createTestRunner()
        runner.run(unitOfWork("context.emit('test-topic', {hamal=nil})"))

        val eventsToEmit = runner.context.eventsToSubmit
        assertThat(eventsToEmit, hasSize(1))

        with(eventsToEmit.first()) {
            assertThat(topicName, equalTo(TopicName("test-topic")))
            assertThat(payload, equalTo(EventPayload()))
        }
    }

    @Test
    fun `Emit event with string payload`() {
        val runner = createTestRunner()
        runner.run(unitOfWork("context.emit('test-topic', {hamal='rocks'})"))

        val eventsToEmit = runner.context.eventsToSubmit
        assertThat(eventsToEmit, hasSize(1))

        with(eventsToEmit.first()) {
            assertThat(topicName, equalTo(TopicName("test-topic")))
            assertThat(payload, equalTo(EventPayload(MapType(mutableMapOf("hamal" to StringType("rocks"))))))
        }
    }

    @Test
    fun `Emit event with number payload`() {
        val runner = createTestRunner()
        runner.run(unitOfWork("context.emit('test-topic',{ answer=42 })"))

        val eventsToEmit = runner.context.eventsToSubmit
        assertThat(eventsToEmit, hasSize(1))

        with(eventsToEmit.first()) {
            assertThat(topicName, equalTo(TopicName("test-topic")))
            assertThat(payload, equalTo(EventPayload(MapType(mutableMapOf("answer" to NumberType(42))))))
        }
    }

    @Test
    fun `Emit event with boolean payload`() {
        val runner = createTestRunner()
        runner.run(unitOfWork("context.emit('test-topic', {true_value=true, false_value=false})"))

        val eventsToEmit = runner.context.eventsToSubmit
        assertThat(eventsToEmit, hasSize(1))

        with(eventsToEmit.first()) {
            assertThat(topicName, equalTo(TopicName("test-topic")))
            assertThat(
                payload, equalTo(
                    EventPayload(
                        MapType(
                            mutableMapOf(
                                "true_value" to True,
                                "false_value" to False
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
        val runner = createTestRunner()
        runner.run(unitOfWork("context.emit('test-topic', { nested_table = { value = 23 } })"))

        val eventsToEmit = runner.context.eventsToSubmit
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
        namespaceId = NamespaceId(9876),
        groupId = GroupId(5432),
        inputs = ExecInputs(),
        state = State(),
        code = CodeValue(code),
        correlation = null
    )
}
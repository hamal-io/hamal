package io.hamal.runner.run.function

import io.hamal.lib.common.value.ValueCode
import io.hamal.lib.common.value.ValueObject
import io.hamal.lib.domain.State
import io.hamal.lib.domain._enum.CodeTypes.Lua54
import io.hamal.lib.domain.vo.CodeType.Companion.CodeType
import io.hamal.lib.domain.vo.EventPayload
import io.hamal.lib.domain.vo.ExecId.Companion.ExecId
import io.hamal.lib.domain.vo.ExecInputs
import io.hamal.lib.domain.vo.ExecResult
import io.hamal.lib.domain.vo.ExecState
import io.hamal.lib.domain.vo.ExecStatusCode.Companion.ExecStatusCode
import io.hamal.lib.domain.vo.ExecToken.Companion.ExecToken
import io.hamal.lib.domain.vo.NamespaceId.Companion.NamespaceId
import io.hamal.lib.domain.vo.TopicName.Companion.TopicName
import io.hamal.lib.domain.vo.WorkspaceId.Companion.WorkspaceId
import io.hamal.runner.connector.UnitOfWork
import io.hamal.runner.run.AbstractTest
import io.hamal.runner.test.TestConnector
import io.hamal.runner.test.TestFailConnector
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test

internal class EmitTest : AbstractTest() {

    @Test
    fun `No events emitted`() {
        runTest(
            unitOfWork(""),
            TestConnector { execId, statusCode, result, state, eventToSubmits ->
                assertThat(execId, equalTo(ExecId(1234)))
                assertThat(statusCode, equalTo(ExecStatusCode(200)))
                assertThat(result, equalTo(ExecResult()))
                assertThat(state, equalTo(ExecState()))
                assertThat(eventToSubmits, hasSize(0))
            })
    }

    @Test
    fun `Emit event without any data`() {
        runTest(
            unitOfWork("context.emit()"),
            TestFailConnector { execId, statusCode, result ->
                assertThat(execId, equalTo(ExecId(1234)))
                assertThat(statusCode, equalTo(ExecStatusCode(400)))
                assertThat(result.value["message"].toString(), containsString("Topic not present"))

            }
        ).also { runner ->
            val eventsToEmit = runner.context.eventsToSubmit
            assertThat(eventsToEmit, hasSize(0))
        }
    }

    @Test
    fun `Emit event without topic`() {
        runTest(
            unitOfWork("context.emit({answer = 42})"),
            TestFailConnector { execId, statusCode, result ->
                assertThat(execId, equalTo(ExecId(1234)))
                assertThat(statusCode, equalTo(ExecStatusCode(400)))
                assertThat(result.value["message"].toString(), containsString("Topic not present"))
            }
        ).also { runner ->
            val eventsToEmit = runner.context.eventsToSubmit
            assertThat(eventsToEmit, hasSize(0))
        }
    }


    @Test
    fun `Emit event without payload`() {
        runTest(unitOfWork("context.emit({topic = 'test-topic'})")).also { runner ->

            val eventsToEmit = runner.context.eventsToSubmit
            assertThat(eventsToEmit, hasSize(1))

            with(eventsToEmit.first()) {
                assertThat(topicName, equalTo(TopicName("test-topic")))
                assertThat(payload, equalTo(EventPayload(ValueObject.builder().build())))
            }
        }
    }


    @Test
    fun `Emit event with nil payload`() {
        runTest(unitOfWork("context.emit({ topic = 'test-topic', hamal=nil })")).also { runner ->
            val eventsToEmit = runner.context.eventsToSubmit
            assertThat(eventsToEmit, hasSize(1))

            with(eventsToEmit.first()) {
                assertThat(topicName, equalTo(TopicName("test-topic")))
                assertThat(payload, equalTo(EventPayload(ValueObject.builder().build())))
            }
        }
    }

    @Test
    fun `Emit event with string payload`() {
        runTest(unitOfWork("context.emit({topic = 'test-topic', hamal='rocks'})")).also { runner ->
            val eventsToEmit = runner.context.eventsToSubmit
            assertThat(eventsToEmit, hasSize(1))

            with(eventsToEmit.first()) {
                assertThat(topicName, equalTo(TopicName("test-topic")))
                assertThat(payload, equalTo(EventPayload(ValueObject.builder().set("hamal", "rocks").build())))
            }
        }
    }

    @Test
    fun `Emit event with number payload`() {
        runTest(unitOfWork("context.emit({ topic = 'test-topic', answer=42 })")).also { runner ->
            val eventsToEmit = runner.context.eventsToSubmit
            assertThat(eventsToEmit, hasSize(1))

            with(eventsToEmit.first()) {
                assertThat(topicName, equalTo(TopicName("test-topic")))
                assertThat(payload, equalTo(EventPayload(ValueObject.builder().set("answer", 42.0).build())))
            }
        }
    }

    @Test
    fun `Emit event with boolean payload`() {
        runTest(unitOfWork("context.emit({topic = 'test-topic', true_value=true, false_value=false})")).also { runner ->
            val eventsToEmit = runner.context.eventsToSubmit
            assertThat(eventsToEmit, hasSize(1))

            with(eventsToEmit.first()) {
                assertThat(topicName, equalTo(TopicName("test-topic")))
                assertThat(
                    payload, equalTo(
                        EventPayload(
                            ValueObject.builder()
                                .set("true_value", true)
                                .set("false_value", false)
                                .build()
                        )
                    )
                )
            }
        }
    }

    @Test
    fun `Emit event with table payload`() {
        runTest(unitOfWork("context.emit({ topic = 'test-topic', nested_table = { value = 23 } })")).also { runner ->
            val eventsToEmit = runner.context.eventsToSubmit
            assertThat(eventsToEmit, hasSize(1))

            with(eventsToEmit.first()) {
                assertThat(topicName, equalTo(TopicName("test-topic")))
                assertThat(
                    payload, equalTo(
                        EventPayload(
                            ValueObject.builder()
                                .set("nested_table", ValueObject.builder().set("value", 23).build())
                                .build()
                        )
                    )
                )
            }
        }
    }


    private fun unitOfWork(code: String) = UnitOfWork(
        id = ExecId(1234),
        execToken = ExecToken("ExecToken"),
        namespaceId = NamespaceId(9876),
        workspaceId = WorkspaceId(5432),
        inputs = ExecInputs(),
        state = State(),
        code = ValueCode(code),
        codeType = CodeType(Lua54),
        correlation = null
    )
}
package io.hamal.runner.run.context

import io.hamal.lib.common.value.*
import io.hamal.lib.domain.State
import io.hamal.lib.domain._enum.CodeType
import io.hamal.lib.domain.vo.EventId.Companion.EventId
import io.hamal.lib.domain.vo.ExecId.Companion.ExecId
import io.hamal.lib.domain.vo.ExecInputs
import io.hamal.lib.domain.vo.ExecToken.Companion.ExecToken
import io.hamal.lib.domain.vo.NamespaceId.Companion.NamespaceId
import io.hamal.lib.domain.vo.TopicId.Companion.TopicId
import io.hamal.lib.domain.vo.TopicName.Companion.TopicName
import io.hamal.lib.domain.vo.WorkspaceId.Companion.WorkspaceId
import io.hamal.lib.kua.function.Function0In0Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.runner.connector.UnitOfWork
import io.hamal.runner.run.AbstractExecuteTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal object EventInvocationTest : AbstractExecuteTest() {


    @Test
    fun `Events available in code`() {
        val testExecutor = createTestRunner()
        testExecutor.run(
            UnitOfWork(
                id = ExecId(1234),
                execToken = ExecToken("ExecToken"),
                namespaceId = NamespaceId(9876),
                workspaceId = WorkspaceId(5432),
                inputs = ExecInputs(
                    ValueObject.builder()
                        .set(
                            "events", ValueArray.builder()
                                .append(
                                    ValueObject.builder()
                                        .set("id", EventId(1234))
                                        .set(
                                            "topic", ValueObject.builder()
                                                .set("id", TopicId(1))
                                                .set("name", TopicName("Topic-One"))
                                                .build()
                                        )
                                        .set("payload", ValueObject.builder().set("block", 43).build())
                                        .build()
                                )
                                .append(
                                    ValueObject.builder()
                                        .set("id", EventId(4321))
                                        .set(
                                            "topic", ValueObject.builder()
                                                .set("id", TopicId(23))
                                                .set("name", TopicName("Topic-Two"))
                                                .build()
                                        )
                                        .set("payload", ValueObject.builder().set("block", 44).build())
                                        .build()
                                )
                                .build()
                        )
                        .build()
                ),
                state = State(),
                code = ValueCode(
                    """
                    assert( context.exec.hook == nil )
                    assert( context.exec.events ~= nil )
                    assert( #context.exec.events == 2 )

                    assert( context.exec.events[1].id == '4d2' )
                    assert( context.exec.events[1].topic.id == '1' )
                    assert( context.exec.events[1].topic.name == 'Topic-One' )
                    assert( context.exec.events[1].payload.block == 43 )

                    assert( context.exec.events[2].id == '10e1' )
                    assert( context.exec.events[2].topic.id == '17' )
                    assert( context.exec.events[2].topic.name == 'Topic-Two' )
                    assert( context.exec.events[2].payload.block == 44 )

                """.trimIndent()
                ),
                codeType = CodeType.Lua54
            )
        )
    }

    @Test
    fun `Events available in function`() {
        val testFn = TestFunction()

        val testExecutor = createTestRunner(ValueString("fn") to testFn)
        testExecutor.run(
            UnitOfWork(
                id = ExecId(1234),
                execToken = ExecToken("ExecToken"),
                namespaceId = NamespaceId(9876),
                workspaceId = WorkspaceId(5432),
                inputs = ExecInputs(
                    ValueObject.builder()
                        .set(
                            "events", ValueArray.builder()
                                .append(
                                    ValueObject.builder()
                                        .set("id", EventId(1234))
                                        .set(
                                            "topic", ValueObject.builder()
                                                .set("id", TopicId(1))
                                                .set("name", TopicName("Topic-One"))
                                                .build()
                                        )
                                        .set("payload", ValueObject.builder().set("block", 43).build())
                                        .build()
                                )
                                .append(
                                    ValueObject.builder()
                                        .set("id", EventId(4321))
                                        .set(
                                            "topic", ValueObject.builder()
                                                .set("id", TopicId(23))
                                                .set("name", TopicName("Topic-Two"))
                                                .build()
                                        )
                                        .set("payload", ValueObject.builder().set("block", 44).build())
                                        .build()
                                )
                                .build()
                        )
                        .build()
                ),
                state = State(),
                code = ValueCode("require_plugin('test').fn()"),
                codeType = CodeType.Lua54
            )
        )
        assertThat(testFn.result?.value?.size, equalTo(2))

        assertThat(testFn.result!!.asObject(0).getSnowflakeId("id"), equalTo(ValueSnowflakeId("4d2")))
        assertThat(testFn.result!!.asObject(0).getObject("topic").getSnowflakeId("id"), equalTo(ValueSnowflakeId("1")))
        assertThat(testFn.result!!.asObject(0).getObject("topic").getString("name"), equalTo(ValueString("Topic-One")))
        assertThat(testFn.result!!.asObject(1).getObject("topic").getString("name"), equalTo(ValueString("Topic-Two")))
        assertThat(testFn.result!!.asObject(0).getObject("payload").getNumber("block"), equalTo(ValueNumber(43)))
    }

    class TestFunction(var result: ValueArray? = null) : Function0In0Out() {
        override fun invoke(ctx: FunctionContext) {
            val inputs = ctx[ExecInputs::class]
            result = inputs.value["events"] as ValueArray
        }
    }
}

internal object HookInvocationTest : AbstractExecuteTest() {

    @Test
    fun `Hook available in code`() {
        val testExecutor = createTestRunner()
        testExecutor.run(
            UnitOfWork(
                id = ExecId(1234),
                execToken = ExecToken("ExecToken"),
                namespaceId = NamespaceId(9876),
                workspaceId = WorkspaceId(5432),
                inputs = ExecInputs(
                    ValueObject.builder().set(
                        "hook", ValueObject.builder()
                            .set("method", "Delete")
                            .set("headers", ValueObject.builder().set("content-type", "application/json").build())
                            .set("parameters", ValueObject.builder().set("answer", 42).build())
                            .set("content", ValueObject.builder().set("hamal", "rocks").build())
                            .build()
                    ).build()
                ),
                state = State(),
                code = ValueCode(
                    """
                    assert( context.exec.events == nil )
                    assert( context.exec.hook ~= nil )

                    hook = context.exec.hook

                    assert( hook.method == 'Delete')

                    assert( table_length(hook.headers) == 1 )
                    assert( hook.headers['content-type'] == 'application/json')

                    assert( table_length(hook.parameters) == 1 )
                    assert( hook.parameters['answer'] == 42 )

                    assert( table_length(hook.content) == 1 )
                    assert( hook.content['hamal'] == 'rocks' )

                """.trimIndent()
                ),
                codeType = CodeType.Lua54
            )
        )
    }

    @Test
    fun `Hook available in function`() {
        val testFn = TestFunction()

        val testExecutor = createTestRunner(ValueString("fn") to testFn)
        testExecutor.run(
            UnitOfWork(
                id = ExecId(1234),
                execToken = ExecToken("ExecToken"),
                namespaceId = NamespaceId(9876),
                workspaceId = WorkspaceId(5432),
                inputs = ExecInputs(
                    ValueObject.builder().set(
                        "hook", ValueObject.builder()
                            .set("method", "Delete")
                            .set("headers", ValueObject.builder().set("content-type", "application/json").build())
                            .set("parameters", ValueObject.builder().set("answer", 42).build())
                            .set("content", ValueObject.builder().set("hamal", "rocks").build())
                            .build()
                    ).build()
                ),
                state = State(),
                code = ValueCode("require_plugin('test').fn()"),
                codeType = CodeType.Lua54
            )
        )
        assertThat(testFn.method, equalTo(ValueString("Delete")))
        assertThat(testFn.headers, equalTo(ValueObject.builder().set("content-type", "application/json").build()))
        assertThat(testFn.parameters, equalTo(ValueObject.builder().set("answer", 42).build()))
        assertThat(testFn.content, equalTo(ValueObject.builder().set("hamal", "rocks").build()))
    }

    class TestFunction(
        var method: ValueString? = null,
        var headers: ValueObject? = null,
        var parameters: ValueObject? = null,
        var content: ValueObject? = null
    ) : Function0In0Out() {
        override fun invoke(ctx: FunctionContext) {
            val inputs = ctx[ExecInputs::class]
            method = inputs.value.getObject("hook").getString("method")
            headers = inputs.value.getObject("hook").getObject("headers")
            parameters = inputs.value.getObject("hook").getObject("parameters")
            content = inputs.value.getObject("hook").getObject("content")
        }
    }

}

internal object EndpointInvocationTest : AbstractExecuteTest() {

    @Test
    fun `Endpoint available in code`() {
        val testExecutor = createTestRunner()
        testExecutor.run(
            UnitOfWork(
                id = ExecId(1234),
                execToken = ExecToken("ExecToken"),
                namespaceId = NamespaceId(9876),
                workspaceId = WorkspaceId(5432),
                inputs = ExecInputs(
                    ValueObject.builder().set(
                        "endpoint", ValueObject.builder()
                            .set("method", "Delete")
                            .set("headers", ValueObject.builder().set("content-type", "application/json").build())
                            .set("parameters", ValueObject.builder().set("answer", "42").build())
                            .set("content", ValueObject.builder().set("hamal", "rocks").build())
                            .build()
                    ).build()
                ),
                state = State(),
                code = ValueCode(
                    """
                    assert( context.exec.events == nil )
                    assert( context.exec.endpoint ~= nil )

                    endpoint = context.exec.endpoint

                    assert( endpoint.method == 'Delete')

                    assert( table_length(endpoint.headers) == 1 )
                    assert( endpoint.headers['content-type'] == 'application/json')

                    assert( table_length(endpoint.parameters) == 1 )
                    assert( endpoint.parameters['answer'] == '42' )

                    assert( table_length(endpoint.content) == 1 )
                    assert( endpoint.content['hamal'] == 'rocks' )

                """.trimIndent()
                ),
                codeType = CodeType.Lua54
            )
        )
    }

    @Test
    fun `Endpoint available in function`() {
        val testFn = TestFunction()

        val testExecutor = createTestRunner(ValueString("fn") to testFn)
        testExecutor.run(
            UnitOfWork(
                id = ExecId(1234),
                execToken = ExecToken("ExecToken"),
                namespaceId = NamespaceId(9876),
                workspaceId = WorkspaceId(5432),
                inputs = ExecInputs(
                    ValueObject.builder().set(
                        "endpoint", ValueObject.builder()
                            .set("method", "Delete")
                            .set("headers", ValueObject.builder().set("content-type", "application/json").build())
                            .set("parameters", ValueObject.builder().set("answer", "42").build())
                            .set("content", ValueObject.builder().set("hamal", "rocks").build())
                            .build()
                    ).build()
                ),
                state = State(),
                code = ValueCode("require_plugin('test').fn()"),
                codeType = CodeType.Lua54
            )
        )
        assertThat(testFn.method, equalTo(ValueString("Delete")))
        assertThat(testFn.headers, equalTo(ValueObject.builder().set("content-type", "application/json").build()))
        assertThat(testFn.parameters, equalTo(ValueObject.builder().set("answer", "42").build()))
        assertThat(testFn.content, equalTo(ValueObject.builder().set("hamal", "rocks").build()))
    }

    class TestFunction(
        var method: ValueString? = null,
        var headers: ValueObject? = null,
        var parameters: ValueObject? = null,
        var content: ValueObject? = null
    ) : Function0In0Out() {
        override fun invoke(ctx: FunctionContext) {
            val inputs = ctx[ExecInputs::class]
            method = inputs.value.getObject("endpoint").getString("method")
            headers = inputs.value.getObject("endpoint").getObject("headers")
            parameters = inputs.value.getObject("endpoint").getObject("parameters")
            content = inputs.value.getObject("endpoint").getObject("content")
        }
    }

}
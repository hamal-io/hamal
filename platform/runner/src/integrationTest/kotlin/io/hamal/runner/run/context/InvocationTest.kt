package io.hamal.runner.run.context

import io.hamal.lib.domain.State
import io.hamal.lib.domain._enum.HookMethod
import io.hamal.lib.domain.vo.*
import io.hamal.lib.kua.function.Function0In0Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.NumberType
import io.hamal.lib.kua.type.StringType
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
                flowId = FlowId(9876),
                groupId = GroupId(5432),
                inputs = ExecInputs(),
                state = State(),
                code = CodeValue(
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
                    
                    assert(context.api.host == 'http://test-api')
                """.trimIndent()
                ),
                invocation = EventInvocation(events),
                apiHost = ApiHost("http://test-api")
            )
        )
    }

    @Test
    fun `Events available in function`() {
        val testFn = TestFunction()

        val testExecutor = createTestRunner("fn" to testFn)
        testExecutor.run(
            UnitOfWork(
                id = ExecId(1234),
                flowId = FlowId(9876),
                groupId = GroupId(5432),
                inputs = ExecInputs(),
                state = State(),
                code = CodeValue("require('test').fn()"),
                invocation = EventInvocation(events),
                apiHost = ApiHost("http://test-api")
            )
        )
        assertThat(testFn.result, equalTo(events))
    }

    class TestFunction(var result: List<Event>? = null) : Function0In0Out() {
        override fun invoke(ctx: FunctionContext) {
            val invocation = ctx[Invocation::class]
            check(invocation is EventInvocation)
            result = invocation.events
        }
    }

    private val events = listOf(
        Event(
            topic = EventTopic(
                id = TopicId(1), name = TopicName("Topic-One")
            ), id = EventId(1234), payload = EventPayload(MapType(mutableMapOf("block" to NumberType(43))))
        ), Event(
            topic = EventTopic(
                id = TopicId(23), name = TopicName("Topic-Two")
            ), id = EventId(4321), payload = EventPayload(MapType(mutableMapOf("block" to NumberType(44))))
        )
    )
}

internal object HookInvocationTest : AbstractExecuteTest() {

    @Test
    fun `Hook available in code`() {
        val testExecutor = createTestRunner()
        testExecutor.run(
            UnitOfWork(
                id = ExecId(1234),
                flowId = FlowId(9876),
                groupId = GroupId(5432),
                inputs = ExecInputs(),
                state = State(),
                code = CodeValue(
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
                invocation = HookInvocation(
                    method = HookMethod.Delete,
                    headers = HookHeaders(MapType(mutableMapOf("content-type" to StringType("application/json")))),
                    parameters = HookParameters(MapType(mutableMapOf("answer" to NumberType(42)))),
                    content = HookContent(MapType(mutableMapOf("hamal" to StringType("rocks"))))
                ),
                apiHost = ApiHost("http://test-api")
            )
        )
    }

    @Test
    fun `Hook available in function`() {
        val testFn = TestFunction()

        val testExecutor = createTestRunner("fn" to testFn)
        testExecutor.run(
            UnitOfWork(
                id = ExecId(1234),
                flowId = FlowId(9876),
                groupId = GroupId(5432),
                inputs = ExecInputs(),
                state = State(),
                code = CodeValue("require('test').fn()"),
                invocation = HookInvocation(
                    method = HookMethod.Delete,
                    headers = HookHeaders(MapType(mutableMapOf("content-type" to StringType("application/json")))),
                    parameters = HookParameters(MapType(mutableMapOf("answer" to NumberType(42)))),
                    content = HookContent(MapType(mutableMapOf("hamal" to StringType("rocks"))))
                ),
                apiHost = ApiHost("http://test-api")
            )
        )
        assertThat(testFn.method, equalTo(HookMethod.Delete))
        assertThat(
            testFn.headers,
            equalTo(HookHeaders(MapType(mutableMapOf("content-type" to StringType("application/json")))))
        )
        assertThat(testFn.parameters, equalTo(HookParameters(MapType(mutableMapOf("answer" to NumberType(42))))))
        assertThat(testFn.content, equalTo(HookContent(MapType(mutableMapOf("hamal" to StringType("rocks"))))))
    }

    class TestFunction(
        var method: HookMethod? = null,
        var headers: HookHeaders? = null,
        var parameters: HookParameters? = null,
        var content: HookContent? = null
    ) : Function0In0Out() {
        override fun invoke(ctx: FunctionContext) {
            val invocation = ctx[Invocation::class]
            check(invocation is HookInvocation)
            method = invocation.method
            headers = invocation.headers
            parameters = invocation.parameters
            content = invocation.content
        }
    }

}
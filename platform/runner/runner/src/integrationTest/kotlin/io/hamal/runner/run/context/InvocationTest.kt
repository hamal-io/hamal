package io.hamal.runner.run.context

import io.hamal.lib.common.hot.HotObject
import io.hamal.lib.domain.State
import io.hamal.lib.domain._enum.EndpointMethod
import io.hamal.lib.domain._enum.HookMethod
import io.hamal.lib.domain.vo.*
import io.hamal.lib.kua.function.Function0In0Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.type.KuaString
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
                    
                """.trimIndent()
                ),
                invocation = Invocation.Event(events)
            )
        )
    }

    @Test
    fun `Events available in function`() {
        val testFn = TestFunction()

        val testExecutor = createTestRunner(KuaString("fn") to testFn)
        testExecutor.run(
            UnitOfWork(
                id = ExecId(1234),
                execToken = ExecToken("ExecToken"),
                namespaceId = NamespaceId(9876),
                workspaceId = WorkspaceId(5432),
                inputs = ExecInputs(),
                state = State(),
                code = CodeValue("require_plugin('test').fn()"),
                invocation = Invocation.Event(events)
            )
        )
        assertThat(testFn.result, equalTo(events))
    }

    class TestFunction(var result: List<Event>? = null) : Function0In0Out() {
        override fun invoke(ctx: FunctionContext) {
            val invocation = ctx[Invocation::class]
            check(invocation is Invocation.Event)
            result = invocation.events
        }
    }

    private val events = listOf(
        Event(
            topic = EventTopic(
                id = TopicId(1), name = TopicName("Topic-One")
            ), id = EventId(1234), payload = EventPayload(HotObject.builder().set("block", 43).build())
        ), Event(
            topic = EventTopic(
                id = TopicId(23), name = TopicName("Topic-Two")
            ), id = EventId(4321), payload = EventPayload(HotObject.builder().set("block", 44).build())
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
                execToken = ExecToken("ExecToken"),
                namespaceId = NamespaceId(9876),
                workspaceId = WorkspaceId(5432),
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
                invocation = Invocation.Hook(
                    method = HookMethod.Delete,
                    headers = HookHeaders(HotObject.builder().set("content-type", "application/json").build()),
                    parameters = HookParameters(HotObject.builder().set("answer", 42).build()),
                    content = HookContent(HotObject.builder().set("hamal", "rocks").build())
                )
            )
        )
    }

    @Test
    fun `Hook available in function`() {
        val testFn = TestFunction()

        val testExecutor = createTestRunner(KuaString("fn") to testFn)
        testExecutor.run(
            UnitOfWork(
                id = ExecId(1234),
                execToken = ExecToken("ExecToken"),
                namespaceId = NamespaceId(9876),
                workspaceId = WorkspaceId(5432),
                inputs = ExecInputs(),
                state = State(),
                code = CodeValue("require_plugin('test').fn()"),
                invocation = Invocation.Hook(
                    method = HookMethod.Delete,
                    headers = HookHeaders(HotObject.builder().set("content-type", "application/json").build()),
                    parameters = HookParameters(HotObject.builder().set("answer", 42).build()),
                    content = HookContent(HotObject.builder().set("hamal", "rocks").build())
                )
            )
        )
        assertThat(testFn.method, equalTo(HookMethod.Delete))
        assertThat(
            testFn.headers,
            equalTo(HookHeaders(HotObject.builder().set("content-type", "application/json").build()))
        )
        assertThat(testFn.parameters, equalTo(HookParameters(HotObject.builder().set("answer", 42).build())))
        assertThat(testFn.content, equalTo(HookContent(HotObject.builder().set("hamal", "rocks").build())))
    }

    class TestFunction(
        var method: HookMethod? = null,
        var headers: HookHeaders? = null,
        var parameters: HookParameters? = null,
        var content: HookContent? = null
    ) : Function0In0Out() {
        override fun invoke(ctx: FunctionContext) {
            val invocation = ctx[Invocation::class]
            check(invocation is Invocation.Hook)
            method = invocation.method
            headers = invocation.headers
            parameters = invocation.parameters
            content = invocation.content
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
                inputs = ExecInputs(),
                state = State(),
                code = CodeValue(
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
                invocation = Invocation.Endpoint(
                    method = EndpointMethod.Delete,
                    headers = EndpointHeaders(HotObject.builder().set("content-type", "application/json").build()),
                    parameters = EndpointParameters(HotObject.builder().set("answer", "42").build()),
                    content = EndpointContent(HotObject.builder().set("hamal", "rocks").build())
                )
            )
        )
    }

    @Test
    fun `Endpoint available in function`() {
        val testFn = TestFunction()

        val testExecutor = createTestRunner(KuaString("fn") to testFn)
        testExecutor.run(
            UnitOfWork(
                id = ExecId(1234),
                execToken = ExecToken("ExecToken"),
                namespaceId = NamespaceId(9876),
                workspaceId = WorkspaceId(5432),
                inputs = ExecInputs(),
                state = State(),
                code = CodeValue("require_plugin('test').fn()"),
                invocation = Invocation.Endpoint(
                    method = EndpointMethod.Delete,
                    headers = EndpointHeaders(HotObject.builder().set("content-type", "application/json").build()),
                    parameters = EndpointParameters(HotObject.builder().set("answer", "42").build()),
                    content = EndpointContent(HotObject.builder().set("hamal", "rocks").build())
                )
            )
        )
        assertThat(testFn.method, equalTo(EndpointMethod.Delete))
        assertThat(
            testFn.headers, equalTo(
                EndpointHeaders(HotObject.builder().set("content-type", "application/json").build())
            )
        )
        assertThat(testFn.parameters, equalTo(EndpointParameters(HotObject.builder().set("answer", "42").build())))
        assertThat(testFn.content, equalTo(EndpointContent(HotObject.builder().set("hamal", "rocks").build())))
    }

    class TestFunction(
        var method: EndpointMethod? = null,
        var headers: EndpointHeaders? = null,
        var parameters: EndpointParameters? = null,
        var content: EndpointContent? = null
    ) : Function0In0Out() {
        override fun invoke(ctx: FunctionContext) {
            val invocation = ctx[Invocation::class]
            check(invocation is Invocation.Endpoint)
            method = invocation.method
            headers = invocation.headers
            parameters = invocation.parameters
            content = invocation.content
        }
    }

}
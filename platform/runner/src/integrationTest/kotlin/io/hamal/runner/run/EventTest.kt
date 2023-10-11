package io.hamal.runner.run

import io.hamal.lib.domain.*
import io.hamal.lib.domain.vo.*
import io.hamal.lib.kua.function.Function0In0Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.type.MapType
import io.hamal.lib.kua.type.NumberType
import io.hamal.runner.connector.UnitOfWork
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class EventTest : AbstractExecuteTest() {

    @Test
    fun `events available in code`() {
        val testExecutor = createTestRunner()
        testExecutor.run(
            UnitOfWork(
                id = ExecId(1234),
                groupId = GroupId(5432),
                inputs = ExecInputs(),
                state = State(),
                code = CodeValue(
                    """
                    assert( ctx.events ~= nil )
                    assert( #ctx.events == 2 )
                    
                    assert( ctx.events[1].id == '4d2' )
                    assert( ctx.events[1].topic.id == '1' )
                    assert( ctx.events[1].topic.name == 'Topic-One' )
                    assert( ctx.events[1].payload.block == 43 )

                    assert( ctx.events[2].id == '10e1' )
                    assert( ctx.events[2].topic.id == '17' )
                    assert( ctx.events[2].topic.name == 'Topic-Two' )
                    assert( ctx.events[2].payload.block == 44 )
                """.trimIndent()
                ),
                events = events,
            )
        )
    }

    @Test
    fun `events available in function`() {
        val testFn = TestFunction()

        val testExecutor = createTestRunner("fn" to testFn)
        testExecutor.run(
            UnitOfWork(
                id = ExecId(1234),
                groupId = GroupId(5432),
                inputs = ExecInputs(),
                state = State(),
                code = CodeValue("test.fn()"),
                events = events,
            )
        )
        assertThat(testFn.result, equalTo(events))
    }

    class TestFunction(var result: List<Event>? = null) : Function0In0Out() {
        override fun invoke(ctx: FunctionContext) {
            val invocationEvents = ctx[RunnerInvocationEvents::class]
            result = invocationEvents.events
        }
    }

    private val events = listOf(
        Event(
            topic = EventTopic(
                id = TopicId(1),
                name = TopicName("Topic-One")
            ),
            id = EventId(1234),
            payload = EventPayload(MapType(mutableMapOf("block" to NumberType(43))))
        ),
        Event(
            topic = EventTopic(
                id = TopicId(23),
                name = TopicName("Topic-Two")
            ),
            id = EventId(4321),
            payload = EventPayload(MapType(mutableMapOf("block" to NumberType(44))))
        )
    )
}
package io.hamal.runner.run

import io.hamal.lib.domain.Event
import io.hamal.lib.domain.EventInvocation
import io.hamal.lib.domain.Invocation
import io.hamal.lib.domain.State
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.ExecInputs
import io.hamal.lib.kua.function.Function0In0Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.type.CodeType
import io.hamal.lib.kua.type.NumberType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.kua.type.DepTableType
import io.hamal.runner.connector.UnitOfWork
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class EventTest : AbstractExecuteTest() {

    @Test
    fun `events available in code`() {
        val testExecutor = createTestExecutor()
        testExecutor(
            UnitOfWork(
                id = ExecId(1234),
                inputs = ExecInputs(),
                state = State(),
                code = CodeType(
                    """
                    assert( ctx.events ~= nil )
                    assert( #ctx.events == 2 )
                    assert( ctx.events[1].topic == 'Topic-One' )
                    assert( ctx.events[1].block == 43 )
                    assert( ctx.events[2].topic == 'Topic-Two' )
                    assert( ctx.events[2].block == 44 )
                """.trimIndent()
                ),
                invocation = EventInvocation(events),
            )
        )
    }

    @Test
    fun `events available in function`() {
        val testFn = TestFunction()

        val testExecutor = createTestExecutor("fn" to testFn)
        testExecutor(
            UnitOfWork(
                id = ExecId(1234),
                inputs = ExecInputs(),
                state = State(),
                code = CodeType("test.fn()"),
                invocation = EventInvocation(events),
            )
        )
        assertThat(testFn.result, equalTo(events))
    }

    class TestFunction(var result: List<Event>? = null) : Function0In0Out() {
        override fun invoke(ctx: FunctionContext) {
            val invocation = ctx[Invocation::class]
            result = (invocation as EventInvocation).events
        }
    }

    private val events = listOf(
        Event(DepTableType("topic" to StringType("Topic-One"), "block" to NumberType(43))),
        Event(DepTableType("topic" to StringType("Topic-Two"), "block" to NumberType(44)))
    )
}
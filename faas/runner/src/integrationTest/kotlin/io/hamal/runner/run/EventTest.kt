package io.hamal.runner.run

import io.hamal.lib.domain.Event
import io.hamal.lib.domain.EventInvocation
import io.hamal.lib.domain.State
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.ExecInputs
import io.hamal.lib.kua.type.CodeType
import io.hamal.lib.kua.type.NumberType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.kua.type.TableType
import io.hamal.runner.connector.UnitOfWork
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
                    assert(ctx.events ~= nil)
                """.trimIndent()
                ),
                invocation = EventInvocation(
                    listOf(
                        Event(
                            TableType(
                                "topic" to StringType("Topic-One"),
                                "block" to NumberType(43)
                            )
                        ),
                        Event(
                            TableType(
                                "topic" to StringType("Topic-One"),
                                "block" to NumberType(44)
                            )
                        )
                    )
                ),
            )
        )
    }

//    @Test
//    fun `exec_id available in function`() {
//        val testFn = TestFunction()
//
//        val testExecutor = createTestExecutor("fn" to testFn)
//        testExecutor(
//            UnitOfWork(
//                id = ExecId(1234),
//                inputs = ExecInputs(),
//                state = State(),
//                code = CodeType("test.fn()"),
//                correlation = null
//            )
//        )
//        assertThat(testFn.result, equalTo("4d2"))
//    }
//
//    class TestFunction(var result: String? = null) : Function0In0Out() {
//        override fun invoke(ctx: FunctionContext) {
//            result = ctx[ExecId::class].value.value.toString(16)
//        }
//    }
}
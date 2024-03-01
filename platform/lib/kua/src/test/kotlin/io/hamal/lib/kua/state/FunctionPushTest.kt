package io.hamal.lib.kua.state

import io.hamal.lib.kua.StackTop
import io.hamal.lib.kua.function.Function0In0Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.type.KuaFunction
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.TestFactory

internal class FunctionPushTest : StateBaseTest() {

    @TestFactory
    fun `Pushes value on stack`() = runTest { testInstance ->
        val result = testInstance.functionPush(TestFunction)
        assertThat(result, equalTo(StackTop(1)))
        assertThat(testInstance.topGet(), equalTo(StackTop(1)))
        assertThat(testInstance.type(1), equalTo(KuaFunction::class))
    }

    object TestFunction : Function0In0Out() {
        override fun invoke(ctx: FunctionContext) {
        }
    }
}
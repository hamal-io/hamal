package io.hamal.lib.kua.native

import io.hamal.lib.kua.ErrorIllegalArgument
import io.hamal.lib.kua.function.Function0In0Out
import io.hamal.lib.kua.function.FunctionContext
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class FunctionPushTest : NativeBaseTest() {

    @Test
    fun `Pushes function on stack`() {
        val result = testInstance.functionPush(TestFunction)
        assertThat(result, equalTo(1))
        assertThat(testInstance.topGet(), equalTo(1))
    }

    @Test
    fun `Tries to push too many items on the stack limited to 999_999`() {
        repeat(999999) { testInstance.booleanPush(true) }

        assertThrows<ErrorIllegalArgument> { testInstance.functionPush(TestFunction) }
            .also { exception -> assertThat(exception.message, equalTo("Prevented stack overflow")) }
    }

    private object TestFunction : Function0In0Out() {
        override fun invoke(ctx: FunctionContext) {}
    }
}

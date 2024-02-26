package io.hamal.lib.kua

import io.hamal.lib.kua.NativeLoader.Preference.Resources
import io.hamal.lib.kua.function.Function0In0Out
import io.hamal.lib.kua.function.FunctionContext
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class InvokeFuncValueTest : BaseStateTest() {
    @Test
    fun `Invoke FuncValue without parameter`() {
        val testFunc = object : Function0In0Out() {
            override fun invoke(ctx: FunctionContext) {
                called = true
            }

            var called = false
        }

        testInstance.functionPush(testFunc)
        testInstance.globalSet("test_func")
        testInstance.stringLoad("test_func()".trimIndent())
        testInstance.functionCall(0, 0)
        assertThat("testFunc was called", testFunc.called, equalTo(true))

        verifyStackIsEmpty()
    }
}

internal class CallFunctionTest : BaseStateTest() {
    @Test
    fun `Creates and invokes a lua function without parameter`() {
        testInstance.stringLoad("function answer() return 42 end")
        testInstance.functionCall(0, 0)

        testInstance.globalGet("answer")
        testInstance.functionCall(0, 1)

        assertThat("The universal answer", testInstance.numberGet(1), equalTo(42.0))
        testInstance.topPop(1)

        verifyStackIsEmpty()
    }
}

internal class GlobalTest : BaseStateTest() {
    @Test
    fun `Sets and gets globals`() {
        testInstance.stringPush("Hamal")
        testInstance.globalSet("name")
        verifyStackIsEmpty()

        testInstance.globalGet("name")
        assertThat(testInstance.type(1), equalTo(4))
        assertThat(testInstance.stringGet(1), equalTo("Hamal"))

        testInstance.topPop(1)
        verifyStackIsEmpty()
    }
}

internal class TableTest : BaseStateTest() {
    @Test
    fun `Sets and gets value from table`() {
        testInstance.tableCreate(0, 1)
        testInstance.stringPush("value")
        testInstance.tabletSetField(1, "key")
        testInstance.tableGetField(1, "key")
        assertThat(testInstance.stringGet(-1), equalTo("value"))

        assertThat(testInstance.tableGetLength(1), equalTo(1))

        testInstance.topPop(2)
        verifyStackIsEmpty()
    }
}

internal sealed class BaseStateTest {
    val testInstance: Native = run {
        NativeLoader.load(Resources)
        Native(Sandbox(NopSandboxContext()))
    }

    fun verifyStackIsEmpty() {
        assertThat("Stack is empty", testInstance.topGet(), equalTo(0))
    }
}

package io.hamal.lib.kua

import io.hamal.lib.kua.value.Function0Param0Result
import io.hamal.lib.kua.value.function.Context
import io.hamal.lib.kua.value.function.FunctionInput0
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class InvokeFuncValueTest : BaseStateTest() {
    @Test
    fun `Invoke FuncValue without parameter`() {
        val testFunc = object : Function0Param0Result() {
            override fun run(ctx: Context, input: FunctionInput0) {
                called = true
            }

            var called = false
        }

        testInstance.pushFunctionValue(testFunc)
        testInstance.setGlobal("test_func")
        testInstance.loadString("test_func()".trimIndent())
        testInstance.call(0, 0)
        assertThat("testFunc was called", testFunc.called, equalTo(true))

        verifyStackIsEmpty()
    }
}

internal class CallFunctionTest : BaseStateTest() {
    @Test
    fun `Creates and invokes a lua function without parameter`() {
        testInstance.loadString("function answer() return 42 end")
        testInstance.call(0, 0)

        testInstance.getGlobal("answer")
        testInstance.call(0, 1)

        assertThat("The universal answer", testInstance.toNumber(1), equalTo(42.0))
        testInstance.pop(1)

        verifyStackIsEmpty()
    }
}

internal class GlobalTest : BaseStateTest() {
    @Test
    fun `Sets and gets globals`() {
        testInstance.pushString("Hamal")
        testInstance.setGlobal("name")
        verifyStackIsEmpty()

        testInstance.getGlobal("name")
        assertThat(testInstance.type(1), equalTo(4))
        assertThat(testInstance.toString(1), equalTo("Hamal"))

        testInstance.pop(1)
        verifyStackIsEmpty()
    }
}

internal class TableTest : BaseStateTest() {
    @Test
    fun `Sets and gets value from table`() {
        testInstance.createTable(0, 1)
        testInstance.pushString("value")
        testInstance.setTableField(1, "key")
        testInstance.getTableField(1, "key")
        assertThat(testInstance.toString(-1), equalTo("value"))

        assertThat(testInstance.getTableLength(1), equalTo(1))

        testInstance.pop(2)
        verifyStackIsEmpty()
    }
}

internal sealed class BaseStateTest {
    val testInstance: Bridge = run {
        ResourceLoader.load()
        Bridge()
    }

    fun verifyStackIsEmpty() {
        assertThat("Stack is empty", testInstance.top(), equalTo(0))
    }
}

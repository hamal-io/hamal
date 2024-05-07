package io.hamal.lib.kua.state

import io.hamal.lib.common.value.*
import io.hamal.lib.kua.*
import io.hamal.lib.kua.function.Function0In0Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.value.KuaTable
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.TestFactory

internal class SetTest : StateBaseTest() {

    @TestFactory
    fun `Boolean`() = runTest { testInstance ->
        testInstance.push(ValueTrue)
        assertThat(testInstance.booleanGet(1), equalTo(ValueTrue))
    }

    @TestFactory
    fun `Decimal`() = runTest { testInstance ->
        testInstance.push(ValueDecimal(231123))
        assertThat(testInstance.decimalGet(1), equalTo(ValueDecimal(231123)))
    }

    @TestFactory
    fun `Error`() = runTest { testInstance ->
        testInstance.push(ValueError("Some Error Message"))
        assertThat(testInstance.errorGet(1), equalTo(ValueError("Some Error Message")))
    }

    @TestFactory
    fun `Function`() = runTest { testInstance ->
        testInstance.push(TestFunction)
        assertThat(testInstance.topGet(), equalTo(StackTop(1)))
    }


    @TestFactory
    fun `Nil`() = runTest { testInstance ->
        testInstance.push(ValueNil)
        assertThat(testInstance.topGet(), equalTo(StackTop(1)))
    }

    @TestFactory
    fun `Number`() = runTest { testInstance ->
        testInstance.push(ValueNumber(231123))
        assertThat(testInstance.numberGet(1), equalTo(ValueNumber(231123)))
    }

    @TestFactory
    fun `String`() = runTest { testInstance ->
        testInstance.push(ValueString("Hamal Rocks"))
        assertThat(testInstance.stringGet(1), equalTo(ValueString("Hamal Rocks")))
    }

    @TestFactory
    fun `Table`() = runTest { testInstance ->
        testInstance.tableCreate(0, 0)
        testInstance.push(KuaTable(1, testInstance))
        testInstance.tableGet(1).also { table ->
            assertThat(table.index, equalTo(ValueNumber(1)))
        }
    }

    object TestFunction : Function0In0Out() {
        override fun invoke(ctx: FunctionContext) {
        }
    }
}
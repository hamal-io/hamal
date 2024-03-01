package io.hamal.lib.kua.state

import io.hamal.lib.kua.StackTop
import io.hamal.lib.kua.function.Function0In0Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.type.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.TestFactory

internal class AnySetTest : StateBaseTest() {

    @TestFactory
    fun `Boolean`() = runTest { testInstance ->
        testInstance.anyPush(KuaAny(KuaTrue))
        assertThat(testInstance.booleanGet(1), equalTo(KuaTrue))
    }

    @TestFactory
    fun `Decimal`() = runTest { testInstance ->
        testInstance.anyPush(KuaAny(KuaDecimal(231123)))
        assertThat(testInstance.decimalGet(1), equalTo(KuaDecimal(231123)))
    }

    @TestFactory
    fun `Error`() = runTest { testInstance ->
        testInstance.anyPush(KuaAny(KuaError("Some Error Message")))
        assertThat(testInstance.errorGet(1), equalTo(KuaError("Some Error Message")))
    }

    @TestFactory
    fun `Function`() = runTest { testInstance ->
        testInstance.anyPush(KuaAny(TestFunction))
        assertThat(testInstance.topGet(), equalTo(StackTop(1)))
    }


    @TestFactory
    fun `Nil`() = runTest { testInstance ->
        testInstance.anyPush(KuaAny(KuaNil))
        assertThat(testInstance.topGet(), equalTo(StackTop(1)))
    }

    @TestFactory
    fun `Number`() = runTest { testInstance ->
        testInstance.anyPush(KuaAny(KuaNumber(231123)))
        assertThat(testInstance.numberGet(1), equalTo(KuaNumber(231123)))
    }

    @TestFactory
    fun `String`() = runTest { testInstance ->
        testInstance.anyPush(KuaAny(KuaString("Hamal Rocks")))
        assertThat(testInstance.stringGet(1), equalTo(KuaString("Hamal Rocks")))
    }

    @TestFactory
    fun `Table`() = runTest { testInstance ->
        testInstance.tableCreate(0, 0)
        testInstance.anyPush(KuaAny(KuaTable(1, testInstance)))
        testInstance.tableGet(1).also { table ->
            assertThat(table.index, equalTo(1))
        }
    }

    object TestFunction : Function0In0Out() {
        override fun invoke(ctx: FunctionContext) {
        }
    }
}
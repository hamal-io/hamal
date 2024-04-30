package io.hamal.lib.kua.state

import io.hamal.lib.kua.StackTop
import io.hamal.lib.kua.tableCreate
import io.hamal.lib.kua.topPop
import io.hamal.lib.kua.type.*
import io.hamal.lib.value.ValueFalse
import io.hamal.lib.value.ValueNil
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.isA
import org.junit.jupiter.api.TestFactory

internal class GlobalGetTest : StateBaseTest() {

    @TestFactory
    fun `Gets global boolean onto stack`() = runTest { testInstance ->
        testInstance.globalSet(KuaString("answer"), ValueFalse)
        assertThat(testInstance.topGet(), equalTo(StackTop(0)))

        testInstance.globalGet(KuaString("answer")).also { result ->
            assertThat(result, equalTo(ValueFalse))
        }
        assertThat(testInstance.topGet(), equalTo(StackTop(1)))
    }

    @TestFactory
    fun `Gets global decimal onto stack`() = runTest { testInstance ->
        testInstance.globalSet(KuaString("answer"), KuaDecimal(12.23))
        assertThat(testInstance.topGet(), equalTo(StackTop(0)))

        testInstance.globalGet(KuaString("answer")).also { result ->
            assertThat(result, equalTo(KuaDecimal(12.23)))
        }
        assertThat(testInstance.topGet(), equalTo(StackTop(1)))
    }

    @TestFactory
    fun `Gets global error onto stack`() = runTest { testInstance ->
        testInstance.globalSet(KuaString("answer"), KuaError("Some Error Message"))
        assertThat(testInstance.topGet(), equalTo(StackTop(0)))

        testInstance.globalGet(KuaString("answer")).also { result ->
            assertThat(result, equalTo(KuaError("Some Error Message")))
        }
        assertThat(testInstance.topGet(), equalTo(StackTop(1)))
    }

    @TestFactory
    fun `Gets global nil onto stack`() = runTest { testInstance ->
        testInstance.globalSet(KuaString("answer"), ValueNil)
        assertThat(testInstance.topGet(), equalTo(StackTop(0)))

        testInstance.globalGet(KuaString("answer")).also { result ->
            assertThat(result, equalTo(ValueNil))
        }
        assertThat(testInstance.topGet(), equalTo(StackTop(1)))
    }

    @TestFactory
    fun `Gets global number onto stack`() = runTest { testInstance ->
        testInstance.globalSet(KuaString("answer"), KuaNumber(42.0))
        assertThat(testInstance.topGet(), equalTo(StackTop(0)))

        testInstance.globalGet(KuaString("answer")).also { result ->
            assertThat(result, equalTo(KuaNumber(42.0)))
        }
        assertThat(testInstance.topGet(), equalTo(StackTop(1)))
    }

    @TestFactory
    fun `Gets global string onto stack`() = runTest { testInstance ->
        testInstance.globalSet(KuaString("answer"), KuaString("hamal rocks"))
        assertThat(testInstance.topGet(), equalTo(StackTop(0)))

        testInstance.globalGet(KuaString("answer")).also { result ->
            assertThat(result, equalTo(KuaString("hamal rocks")))
        }
        assertThat(testInstance.topGet(), equalTo(StackTop(1)))
    }

    @TestFactory
    fun `Gets global table onto stack`() = runTest { testInstance ->
        val table = testInstance.tableCreate(0, 0)
        testInstance.globalSet(KuaString("answer"), table)
        testInstance.topPop(1)
        assertThat(testInstance.topGet(), equalTo(StackTop(0)))

        testInstance.globalGet(KuaString("answer")).also { result ->
            assertThat(result, isA(KuaTable::class.java))
        }
        assertThat(testInstance.topGet(), equalTo(StackTop(1)))
    }
}
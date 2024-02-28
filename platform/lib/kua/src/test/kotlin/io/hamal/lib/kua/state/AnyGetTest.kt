package io.hamal.lib.kua.state

import io.hamal.lib.kua.type.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.TestFactory

internal class AnyGetTest : StateBaseTest() {

    @TestFactory
    fun `Boolean`() = runTest { testInstance ->
        testInstance.booleanPush(KuaTrue)
        testInstance.anyGet(1).also { any ->
            assertThat(any, equalTo(KuaAny(KuaTrue)))
        }
    }

    @TestFactory
    fun `Decimal`() = runTest { testInstance ->
        testInstance.decimalPush(KuaDecimal(231123))
        testInstance.anyGet(1).also { any ->
            assertThat(any, equalTo(KuaAny(KuaDecimal(231123))))
        }
    }

    @TestFactory
    fun `Error`() = runTest { testInstance ->
        testInstance.errorPush(KuaError("Some Error Message"))
        testInstance.anyGet(1).also { any ->
            assertThat(any, equalTo(KuaAny(KuaError("Some Error Message"))))
        }
    }

    @TestFactory
    fun `Nil`() = runTest { testInstance ->
        testInstance.nilPush()
        testInstance.anyGet(1).also { any ->
            assertThat(any, equalTo(KuaAny(KuaNil)))
        }
    }

    @TestFactory
    fun `Number`() = runTest { testInstance ->
        testInstance.numberPush(KuaNumber(231123))
        testInstance.anyGet(1).also { any ->
            assertThat(any, equalTo(KuaAny(KuaNumber(231123))))
        }
    }

    @TestFactory
    fun `String`() = runTest { testInstance ->
        testInstance.stringPush(KuaString("Hamal Rocks"))
        testInstance.anyGet(1).also { any ->
            assertThat(any, equalTo(KuaAny(KuaString("Hamal Rocks"))))
        }
    }

    @TestFactory
    fun `Table`() = runTest { testInstance ->
        testInstance.tableCreate(0, 0)
        testInstance.anyGet(1).also { any ->
            val value = any.value
            require(value is KuaTable)
            assertThat(value.index, equalTo(1))
        }
    }
}
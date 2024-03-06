package io.hamal.lib.kua.state

import io.hamal.lib.kua.get
import io.hamal.lib.kua.tableCreate
import io.hamal.lib.kua.type.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.TestFactory

internal class GetTest : StateBaseTest() {

    @TestFactory
    fun `Boolean`() = runTest { testInstance ->
        testInstance.booleanPush(KuaTrue)
        testInstance.get(1).also { value ->
            assertThat(value, equalTo(KuaTrue))
        }
    }

    @TestFactory
    fun `Decimal`() = runTest { testInstance ->
        testInstance.decimalPush(KuaDecimal(231123))
        testInstance.get(1).also { value ->
            assertThat(value, equalTo(KuaDecimal(231123)))
        }
    }

    @TestFactory
    fun `Error`() = runTest { testInstance ->
        testInstance.errorPush(KuaError("Some Error Message"))
        testInstance.get(1).also { value ->
            assertThat(value, equalTo(KuaError("Some Error Message")))
        }
    }

    @TestFactory
    fun `Nil`() = runTest { testInstance ->
        testInstance.nilPush()
        testInstance.get(1).also { value ->
            assertThat(value, equalTo(KuaNil))
        }
    }

    @TestFactory
    fun `Number`() = runTest { testInstance ->
        testInstance.numberPush(KuaNumber(231123))
        testInstance.get(1).also { value ->
            assertThat(value, equalTo(KuaNumber(231123)))
        }
    }

    @TestFactory
    fun `String`() = runTest { testInstance ->
        testInstance.stringPush(KuaString("Hamal Rocks"))
        testInstance.get(1).also { value ->
            assertThat(value, equalTo(KuaString("Hamal Rocks")))
        }
    }

    @TestFactory
    fun `Table`() = runTest { testInstance ->
        testInstance.tableCreate(0, 0)
        testInstance.get(1).also { value ->
            require(value is KuaTable)
            assertThat(value.index, equalTo(KuaNumber(1)))
        }
    }
}
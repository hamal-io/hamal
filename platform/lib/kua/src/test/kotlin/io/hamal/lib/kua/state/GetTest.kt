package io.hamal.lib.kua.state

import io.hamal.lib.common.value.*
import io.hamal.lib.kua.get
import io.hamal.lib.kua.tableCreate
import io.hamal.lib.kua.value.KuaTable
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.TestFactory

internal class GetTest : StateBaseTest() {

    @TestFactory
    fun `Boolean`() = runTest { testInstance ->
        testInstance.booleanPush(ValueTrue)
        testInstance.get(1).also { value ->
            assertThat(value, equalTo(ValueTrue))
        }
    }

    @TestFactory
    fun `Decimal`() = runTest { testInstance ->
        testInstance.decimalPush(ValueDecimal(231123))
        testInstance.get(1).also { value ->
            assertThat(value, equalTo(ValueDecimal(231123)))
        }
    }

    @TestFactory
    fun `Error`() = runTest { testInstance ->
        testInstance.errorPush(ValueError("Some Error Message"))
        testInstance.get(1).also { value ->
            assertThat(value, equalTo(ValueError("Some Error Message")))
        }
    }

    @TestFactory
    fun `Nil`() = runTest { testInstance ->
        testInstance.nilPush()
        testInstance.get(1).also { value ->
            assertThat(value, equalTo(ValueNil))
        }
    }

    @TestFactory
    fun `Number`() = runTest { testInstance ->
        testInstance.numberPush(ValueNumber(231123))
        testInstance.get(1).also { value ->
            assertThat(value, equalTo(ValueNumber(231123)))
        }
    }

    @TestFactory
    fun `String`() = runTest { testInstance ->
        testInstance.stringPush(ValueString("Hamal Rocks"))
        testInstance.get(1).also { value ->
            assertThat(value, equalTo(ValueString("Hamal Rocks")))
        }
    }

    @TestFactory
    fun `Table`() = runTest { testInstance ->
        testInstance.tableCreate(0, 0)
        testInstance.get(1).also { value ->
            require(value is KuaTable)
            assertThat(value.index, equalTo(ValueNumber(1)))
        }
    }
}
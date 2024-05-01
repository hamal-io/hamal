package io.hamal.lib.kua.state

import io.hamal.lib.kua.function.Function0In0Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.tableCreate
import io.hamal.lib.kua.type
import io.hamal.lib.kua.value.KuaFunction
import io.hamal.lib.kua.value.KuaTable
import io.hamal.lib.value.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.TestFactory

internal class TypeTest : StateBaseTest() {

    @TestFactory
    fun `Boolean`() = runTest { testInstance ->
        testInstance.booleanPush(ValueTrue)
        testInstance.type(1).also { result ->
            assertThat(result, equalTo(ValueBoolean::class))
        }
    }

    @TestFactory
    fun `Decimal`() = runTest { testInstance ->
        testInstance.decimalPush(ValueDecimal(231123))
        testInstance.type(1).also { result ->
            assertThat(result, equalTo(ValueDecimal::class))
        }
    }

    @TestFactory
    fun `Error`() = runTest { testInstance ->
        testInstance.errorPush(ValueError("Some Error Message"))
        testInstance.type(1).also { result ->
            assertThat(result, equalTo(ValueError::class))
        }
    }

    @TestFactory
    fun `Function`() = runTest { testInstance ->
        testInstance.functionPush(TestFunction)
        testInstance.type(1).also { result ->
            assertThat(result, equalTo(KuaFunction::class))
        }
    }

    @TestFactory
    fun `Nil`() = runTest { testInstance ->
        testInstance.nilPush()
        testInstance.type(1).also { result ->
            assertThat(result, equalTo(ValueNil::class))
        }
    }

    @TestFactory
    fun `Number`() = runTest { testInstance ->
        testInstance.numberPush(ValueNumber(231123))
        testInstance.type(1).also { result ->
            assertThat(result, equalTo(ValueNumber::class))
        }
    }

    @TestFactory
    fun `String`() = runTest { testInstance ->
        testInstance.stringPush(ValueString("Hamal Rocks"))
        testInstance.type(1).also { result ->
            assertThat(result, equalTo(ValueString::class))
        }
    }

    @TestFactory
    fun `Table`() = runTest { testInstance ->
        testInstance.tableCreate(0, 0)
        testInstance.type(1).also { result ->
            assertThat(result, equalTo(KuaTable::class))
        }
    }


    object TestFunction : Function0In0Out() {
        override fun invoke(ctx: FunctionContext) {
        }
    }
}
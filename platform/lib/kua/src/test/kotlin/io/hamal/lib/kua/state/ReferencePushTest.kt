package io.hamal.lib.kua.state

import io.hamal.lib.common.value.*
import io.hamal.lib.kua.*
import io.hamal.lib.kua.function.Function0In0Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.value.KuaFunction
import io.hamal.lib.kua.value.KuaTable
import io.hamal.lib.kua.value.getNumber
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.TestFactory

internal class ReferencePushTest : StateBaseTest() {

    @TestFactory
    fun `Pushes reference value of boolean`() = runTest { testInstance ->
        testInstance.booleanPush(ValueTrue)

        val reference = testInstance.referenceAcquire()
        assertThat(testInstance.topGet(), equalTo(StackTop(0)))

        testInstance.referencePush(reference).also { type -> assertThat(type, equalTo(ValueBoolean::class)) }

        assertThat(testInstance.topGet(), equalTo(StackTop(1)))
        assertThat(testInstance.type(1), equalTo(ValueBoolean::class))
        assertThat(testInstance.booleanGet(1), equalTo(ValueTrue))
    }

    @TestFactory
    fun `Pushes reference value of decimal`() = runTest { testInstance ->
        testInstance.decimalPush(ValueDecimal(12.21))

        val reference = testInstance.referenceAcquire()
        assertThat(testInstance.topGet(), equalTo(StackTop(0)))

        testInstance.referencePush(reference).also { type -> assertThat(type, equalTo(ValueDecimal::class)) }

        assertThat(testInstance.topGet(), equalTo(StackTop(1)))
        assertThat(testInstance.type(1), equalTo(ValueDecimal::class))
        assertThat(testInstance.decimalGet(1), equalTo(ValueDecimal(12.21)))
    }

    @TestFactory
    fun `Pushes reference value of error`() = runTest { testInstance ->
        testInstance.errorPush(ValueError("Some Error Message"))

        val reference = testInstance.referenceAcquire()
        assertThat(testInstance.topGet(), equalTo(StackTop(0)))

        testInstance.referencePush(reference).also { type -> assertThat(type, equalTo(ValueError::class)) }

        assertThat(testInstance.topGet(), equalTo(StackTop(1)))
        assertThat(testInstance.type(1), equalTo(ValueError::class))
        assertThat(testInstance.errorGet(1), equalTo(ValueError("Some Error Message")))
    }


    @TestFactory
    fun `Pushes reference value of function`() = runTest { testInstance ->

        val testFn = object : Function0In0Out() {
            override fun invoke(ctx: FunctionContext) {
                TODO("Not yet implemented")
            }
        }

        testInstance.functionPush(testFn)

        val reference = testInstance.referenceAcquire()
        assertThat(testInstance.topGet(), equalTo(StackTop(0)))

        testInstance.referencePush(reference).also { type -> assertThat(type, equalTo(KuaFunction::class)) }

        assertThat(testInstance.topGet(), equalTo(StackTop(1)))
        assertThat(testInstance.type(1), equalTo(KuaFunction::class))
    }

    @TestFactory
    fun `Pushes reference value of nil`() = runTest { testInstance ->
        testInstance.nilPush()
        val reference = testInstance.referenceAcquire()
        assertThat(testInstance.topGet(), equalTo(StackTop(0)))

        testInstance.referencePush(reference).also { type -> assertThat(type, equalTo(ValueNil::class)) }

        assertThat(testInstance.topGet(), equalTo(StackTop(1)))
        assertThat(testInstance.type(1), equalTo(ValueNil::class))
    }

    @TestFactory
    fun `Pushes reference value of number`() = runTest { testInstance ->
        testInstance.numberPush(ValueNumber(23.11))

        val reference = testInstance.referenceAcquire()
        assertThat(testInstance.topGet(), equalTo(StackTop(0)))

        testInstance.referencePush(reference).also { type -> assertThat(type, equalTo(ValueNumber::class)) }

        assertThat(testInstance.topGet(), equalTo(StackTop(1)))
        assertThat(testInstance.type(1), equalTo(ValueNumber::class))
        assertThat(testInstance.numberGet(1), equalTo(ValueNumber(23.11)))
    }

    @TestFactory
    fun `Pushes reference value of string`() = runTest { testInstance ->
        testInstance.stringPush(ValueString("hamal rocks"))

        val reference = testInstance.referenceAcquire()
        assertThat(testInstance.topGet(), equalTo(StackTop(0)))

        testInstance.referencePush(reference).also { type -> assertThat(type, equalTo(ValueString::class)) }

        assertThat(testInstance.topGet(), equalTo(StackTop(1)))
        assertThat(testInstance.type(1), equalTo(ValueString::class))
        assertThat(testInstance.stringGet(1), equalTo(ValueString("hamal rocks")))
    }


    @TestFactory
    fun `Pushes reference value of table`() = runTest { testInstance ->
        testInstance.tableCreate(ValueString("answer") to ValueNumber(42.0))

        val reference = testInstance.referenceAcquire()
        assertThat(testInstance.topGet(), equalTo(StackTop(0)))

        testInstance.referencePush(reference).also { type -> assertThat(type, equalTo(KuaTable::class)) }

        assertThat(testInstance.topGet(), equalTo(StackTop(1)))
        assertThat(testInstance.type(1), equalTo(KuaTable::class))

        KuaTable(1, testInstance).getNumber("answer").also { result ->
            assertThat(result, equalTo(ValueNumber(42.0)))
        }
    }

}
package io.hamal.lib.kua.state

import io.hamal.lib.kua.*
import io.hamal.lib.kua.function.Function0In0Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.type.*
import io.hamal.lib.kua.type.KuaError
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.TestFactory

internal class ReferencePushTest : StateBaseTest() {

    @TestFactory
    fun `Pushes reference value of boolean`() = runTest { testInstance ->
        testInstance.booleanPush(KuaTrue)

        val reference = testInstance.referenceAcquire()
        assertThat(testInstance.topGet(), equalTo(StackTop(0)))

        testInstance.referencePush(reference).also { type -> assertThat(type, equalTo(KuaBoolean::class)) }

        assertThat(testInstance.topGet(), equalTo(StackTop(1)))
        assertThat(testInstance.type(1), equalTo(KuaBoolean::class))
        assertThat(testInstance.booleanGet(1), equalTo(KuaTrue))
    }

    @TestFactory
    fun `Pushes reference value of decimal`() = runTest { testInstance ->
        testInstance.decimalPush(KuaDecimal(12.21))

        val reference = testInstance.referenceAcquire()
        assertThat(testInstance.topGet(), equalTo(StackTop(0)))

        testInstance.referencePush(reference).also { type -> assertThat(type, equalTo(KuaDecimal::class)) }

        assertThat(testInstance.topGet(), equalTo(StackTop(1)))
        assertThat(testInstance.type(1), equalTo(KuaDecimal::class))
        assertThat(testInstance.decimalGet(1), equalTo(KuaDecimal(12.21)))
    }

    @TestFactory
    fun `Pushes reference value of error`() = runTest { testInstance ->
        testInstance.errorPush(KuaError("Some Error Message"))

        val reference = testInstance.referenceAcquire()
        assertThat(testInstance.topGet(), equalTo(StackTop(0)))

        testInstance.referencePush(reference).also { type -> assertThat(type, equalTo(KuaError::class)) }

        assertThat(testInstance.topGet(), equalTo(StackTop(1)))
        assertThat(testInstance.type(1), equalTo(KuaError::class))
        assertThat(testInstance.errorGet(1), equalTo(KuaError("Some Error Message")))
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

        testInstance.referencePush(reference).also { type -> assertThat(type, equalTo(KuaNil::class)) }

        assertThat(testInstance.topGet(), equalTo(StackTop(1)))
        assertThat(testInstance.type(1), equalTo(KuaNil::class))
    }

    @TestFactory
    fun `Pushes reference value of number`() = runTest { testInstance ->
        testInstance.numberPush(KuaNumber(23.11))

        val reference = testInstance.referenceAcquire()
        assertThat(testInstance.topGet(), equalTo(StackTop(0)))

        testInstance.referencePush(reference).also { type -> assertThat(type, equalTo(KuaNumber::class)) }

        assertThat(testInstance.topGet(), equalTo(StackTop(1)))
        assertThat(testInstance.type(1), equalTo(KuaNumber::class))
        assertThat(testInstance.numberGet(1), equalTo(KuaNumber(23.11)))
    }

    @TestFactory
    fun `Pushes reference value of string`() = runTest { testInstance ->
        testInstance.stringPush(KuaString("hamal rocks"))

        val reference = testInstance.referenceAcquire()
        assertThat(testInstance.topGet(), equalTo(StackTop(0)))

        testInstance.referencePush(reference).also { type -> assertThat(type, equalTo(KuaString::class)) }

        assertThat(testInstance.topGet(), equalTo(StackTop(1)))
        assertThat(testInstance.type(1), equalTo(KuaString::class))
        assertThat(testInstance.stringGet(1), equalTo(KuaString("hamal rocks")))
    }


    @TestFactory
    fun `Pushes reference value of table`() = runTest { testInstance ->
        testInstance.tableCreate(KuaString("answer") to KuaNumber(42.0))

        val reference = testInstance.referenceAcquire()
        assertThat(testInstance.topGet(), equalTo(StackTop(0)))

        testInstance.referencePush(reference).also { type -> assertThat(type, equalTo(KuaTable::class)) }

        assertThat(testInstance.topGet(), equalTo(StackTop(1)))
        assertThat(testInstance.type(1), equalTo(KuaTable::class))

        KuaTable(1, testInstance).getNumber("answer").also { result ->
            assertThat(result, equalTo(KuaNumber(42.0)))
        }
    }

}
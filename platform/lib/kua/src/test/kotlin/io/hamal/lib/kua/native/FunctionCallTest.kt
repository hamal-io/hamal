package io.hamal.lib.kua.native

import io.hamal.lib.kua.function.Function2In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput2Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.value.ValueNumber
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class FunctionCallTest : NativeBaseTest() {

    @Test
    fun `Calls kotlin function with 2 parameter and 2 receives 2 values back`() {
        testInstance.functionPush(TwoInTwoOut())
        testInstance.numberPush(1.0)
        testInstance.numberPush(5.0)
        testInstance.functionCall(2, 2)

        assertThat(testInstance.numberGet(-1), equalTo(2.0))
        assertThat(testInstance.numberGet(-2), equalTo(20.0))

        testInstance.topPop(2)
        verifyStackIsEmpty()
    }

    private class TwoInTwoOut : Function2In2Out<ValueNumber, ValueNumber, ValueNumber, ValueNumber>(
        FunctionInput2Schema(ValueNumber::class, ValueNumber::class),
        FunctionOutput2Schema(ValueNumber::class, ValueNumber::class)
    ) {

        override fun invoke(
            ctx: FunctionContext, arg1: ValueNumber, arg2: ValueNumber
        ): Pair<ValueNumber, ValueNumber> {
            return Pair(arg2 * 4, arg1 * 2)
        }
    }
}


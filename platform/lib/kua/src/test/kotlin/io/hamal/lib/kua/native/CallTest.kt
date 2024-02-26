package io.hamal.lib.kua.native

import io.hamal.lib.kua.function.Function2In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput2Schema
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.type.KuaNumber
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class CallTest : NativeBaseTest() {

    @Test
    fun `Calls kotlin function with 2 parameter and 2 receives 2 values back`() {
        testInstance.pushFunction(TwoInTwoOut())
        testInstance.pushNumber(1.0)
        testInstance.pushNumber(5.0)
        testInstance.call(2, 2)

        assertThat(testInstance.toNumber(-1), equalTo(2.0))
        assertThat(testInstance.toNumber(-2), equalTo(20.0))

        testInstance.pop(2)
        verifyStackIsEmpty()
    }

    private class TwoInTwoOut : Function2In2Out<KuaNumber, KuaNumber, KuaNumber, KuaNumber>(
        FunctionInput2Schema(KuaNumber::class, KuaNumber::class),
        FunctionOutput2Schema(KuaNumber::class, KuaNumber::class)
    ) {

        override fun invoke(
            ctx: FunctionContext, arg1: KuaNumber, arg2: KuaNumber
        ): Pair<KuaNumber, KuaNumber> {
            return Pair(arg2 * 4, arg1 * 2)
        }
    }
}


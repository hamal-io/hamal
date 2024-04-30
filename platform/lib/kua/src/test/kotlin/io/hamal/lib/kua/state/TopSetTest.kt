package io.hamal.lib.kua.state

import io.hamal.lib.kua.StackTop
import io.hamal.lib.kua.decimalGet
import io.hamal.lib.kua.topSet
import io.hamal.lib.kua.type
import io.hamal.lib.kua.type.KuaDecimal
import io.hamal.lib.value.ValueNil
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.TestFactory

internal class TopSetTest : StateBaseTest() {

    @TestFactory
    fun `Can grow a stack`() = runTest { testInstance ->
        testInstance.decimalPush(KuaDecimal(1.0))
        testInstance.decimalPush(KuaDecimal(2.0))

        testInstance.topSet(4)
        assertThat(testInstance.topGet(), equalTo(StackTop(4)))
        assertThat(testInstance.decimalGet(1), equalTo(KuaDecimal(1.0)))
        assertThat(testInstance.decimalGet(2), equalTo(KuaDecimal(2.0)))
        assertThat(testInstance.type(3), equalTo(ValueNil::class))
        assertThat(testInstance.type(4), equalTo(ValueNil::class))
    }


    @TestFactory
    fun `Can shrink a stack`() = runTest { testInstance ->
        testInstance.decimalPush(KuaDecimal(1.0))
        testInstance.decimalPush(KuaDecimal(2.0))
        testInstance.decimalPush(KuaDecimal(3.0))
        testInstance.decimalPush(KuaDecimal(4.0))

        testInstance.topSet(-3)
        assertThat(testInstance.topGet(), equalTo(StackTop(2)))
        assertThat(testInstance.decimalGet(1), equalTo(KuaDecimal(1.0)))
        assertThat(testInstance.decimalGet(2), equalTo(KuaDecimal(2.0)))
    }

}
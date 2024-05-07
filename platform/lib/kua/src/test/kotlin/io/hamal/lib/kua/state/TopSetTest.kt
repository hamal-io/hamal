package io.hamal.lib.kua.state

import io.hamal.lib.common.value.ValueDecimal
import io.hamal.lib.common.value.ValueNil
import io.hamal.lib.kua.StackTop
import io.hamal.lib.kua.decimalGet
import io.hamal.lib.kua.topSet
import io.hamal.lib.kua.type
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.TestFactory

internal class TopSetTest : StateBaseTest() {

    @TestFactory
    fun `Can grow a stack`() = runTest { testInstance ->
        testInstance.decimalPush(ValueDecimal(1.0))
        testInstance.decimalPush(ValueDecimal(2.0))

        testInstance.topSet(4)
        assertThat(testInstance.topGet(), equalTo(StackTop(4)))
        assertThat(testInstance.decimalGet(1), equalTo(ValueDecimal(1.0)))
        assertThat(testInstance.decimalGet(2), equalTo(ValueDecimal(2.0)))
        assertThat(testInstance.type(3), equalTo(ValueNil::class))
        assertThat(testInstance.type(4), equalTo(ValueNil::class))
    }


    @TestFactory
    fun `Can shrink a stack`() = runTest { testInstance ->
        testInstance.decimalPush(ValueDecimal(1.0))
        testInstance.decimalPush(ValueDecimal(2.0))
        testInstance.decimalPush(ValueDecimal(3.0))
        testInstance.decimalPush(ValueDecimal(4.0))

        testInstance.topSet(-3)
        assertThat(testInstance.topGet(), equalTo(StackTop(2)))
        assertThat(testInstance.decimalGet(1), equalTo(ValueDecimal(1.0)))
        assertThat(testInstance.decimalGet(2), equalTo(ValueDecimal(2.0)))
    }

}
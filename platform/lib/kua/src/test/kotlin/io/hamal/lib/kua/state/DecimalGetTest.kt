package io.hamal.lib.kua.state

import io.hamal.lib.kua.StackTop
import io.hamal.lib.kua.type.KuaDecimal
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.TestFactory

internal class DecimalGetTest : StateBaseTest() {

    @TestFactory
    fun `Reads value on stack without popping the value`() = runTest { testInstance ->
        testInstance.decimalPush(KuaDecimal("123"))
        assertThat(testInstance.decimalGet(1), equalTo(KuaDecimal("123")))
        assertThat(testInstance.topGet(), equalTo(StackTop(1)))

        testInstance.decimalPush(KuaDecimal("234"))
        assertThat(testInstance.decimalGet(2), equalTo(KuaDecimal("234")))
        assertThat(testInstance.topGet(), equalTo(StackTop(2)))
    }

    @TestFactory
    fun `Reads value on stack with negative index without popping the value`() = runTest { testInstance ->
        testInstance.decimalPush(KuaDecimal("123"))
        assertThat(testInstance.decimalGet(1), equalTo(KuaDecimal("123")))
        assertThat(testInstance.topGet(), equalTo(StackTop(1)))

        testInstance.decimalPush(KuaDecimal("234"))
        assertThat(testInstance.decimalGet(-1), equalTo(KuaDecimal("234")))
        assertThat(testInstance.topGet(), equalTo(StackTop(2)))
    }
}
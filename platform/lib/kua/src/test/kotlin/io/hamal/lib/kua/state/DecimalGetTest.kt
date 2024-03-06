package io.hamal.lib.kua.state

import io.hamal.lib.kua.StackTop
import io.hamal.lib.kua.decimalGet
import io.hamal.lib.kua.type.KuaDecimal
import io.hamal.lib.kua.type.KuaString
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows

internal class DecimalGetTest : StateBaseTest() {

    @TestFactory
    fun `Get value on stack without popping the value`() = runTest { testInstance ->
        testInstance.decimalPush(KuaDecimal("123"))
        assertThat(testInstance.decimalGet(1), equalTo(KuaDecimal("123")))
        assertThat(testInstance.topGet(), equalTo(StackTop(1)))

        testInstance.decimalPush(KuaDecimal("234"))
        assertThat(testInstance.decimalGet(2), equalTo(KuaDecimal("234")))
        assertThat(testInstance.topGet(), equalTo(StackTop(2)))
    }

    @TestFactory
    fun `Get value with negative index without popping the value`() = runTest { testInstance ->
        testInstance.decimalPush(KuaDecimal("123"))
        assertThat(testInstance.decimalGet(1), equalTo(KuaDecimal("123")))
        assertThat(testInstance.topGet(), equalTo(StackTop(1)))

        testInstance.decimalPush(KuaDecimal("234"))
        assertThat(testInstance.decimalGet(-1), equalTo(KuaDecimal("234")))
        assertThat(testInstance.topGet(), equalTo(StackTop(2)))
    }

    @TestFactory
    fun `Not a decimal`() = runTest { testInstance ->
        testInstance.stringPush(KuaString("Not  a boolean"))
        assertThrows<IllegalStateException> {
            testInstance.decimalGet(1)
        }.also { exception ->
            assertThat(
                exception.message,
                equalTo("Expected type to be decimal but was string")
            )
        }
    }

}
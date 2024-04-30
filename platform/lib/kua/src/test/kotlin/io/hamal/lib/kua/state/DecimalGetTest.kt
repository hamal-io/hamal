package io.hamal.lib.kua.state

import io.hamal.lib.kua.StackTop
import io.hamal.lib.kua.decimalGet
import io.hamal.lib.value.ValueDecimal
import io.hamal.lib.value.ValueString
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows

internal class DecimalGetTest : StateBaseTest() {

    @TestFactory
    fun `Get value on stack without popping the value`() = runTest { testInstance ->
        testInstance.decimalPush(ValueDecimal("123"))
        assertThat(testInstance.decimalGet(1), equalTo(ValueDecimal("123")))
        assertThat(testInstance.topGet(), equalTo(StackTop(1)))

        testInstance.decimalPush(ValueDecimal("234"))
        assertThat(testInstance.decimalGet(2), equalTo(ValueDecimal("234")))
        assertThat(testInstance.topGet(), equalTo(StackTop(2)))
    }

    @TestFactory
    fun `Get value with negative index without popping the value`() = runTest { testInstance ->
        testInstance.decimalPush(ValueDecimal("123"))
        assertThat(testInstance.decimalGet(1), equalTo(ValueDecimal("123")))
        assertThat(testInstance.topGet(), equalTo(StackTop(1)))

        testInstance.decimalPush(ValueDecimal("234"))
        assertThat(testInstance.decimalGet(-1), equalTo(ValueDecimal("234")))
        assertThat(testInstance.topGet(), equalTo(StackTop(2)))
    }

    @TestFactory
    fun `Not a decimal`() = runTest { testInstance ->
        testInstance.stringPush(ValueString("Not  a boolean"))
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
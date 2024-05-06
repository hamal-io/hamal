package io.hamal.lib.kua.state

import io.hamal.lib.common.value.ValueNumber
import io.hamal.lib.common.value.ValueString
import io.hamal.lib.kua.ErrorIllegalState
import io.hamal.lib.kua.StackTop
import io.hamal.lib.kua.numberGet
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows

internal class SerdeNumberGetTest : StateBaseTest() {

    @TestFactory
    fun `Get value on stack without popping the value`() = runTest { testInstance ->
        testInstance.numberPush(ValueNumber(123))
        assertThat(testInstance.numberGet(1), equalTo(ValueNumber(123)))
        assertThat(testInstance.topGet(), equalTo(StackTop(1)))

        testInstance.numberPush(ValueNumber(234))
        assertThat(testInstance.numberGet(2), equalTo(ValueNumber(234)))
        assertThat(testInstance.topGet(), equalTo(StackTop(2)))
    }

    @TestFactory
    fun `Get value with negative index without popping the value`() = runTest { testInstance ->
        testInstance.numberPush(ValueNumber(123))
        assertThat(testInstance.numberGet(1), equalTo(ValueNumber(123)))
        assertThat(testInstance.topGet(), equalTo(StackTop(1)))

        testInstance.numberPush(ValueNumber(234))
        assertThat(testInstance.numberGet(-1), equalTo(ValueNumber(234)))
        assertThat(testInstance.topGet(), equalTo(StackTop(2)))
    }

    @TestFactory
    fun `Not a number`() = runTest { testInstance ->
        testInstance.stringPush(ValueString("Not  a boolean"))
        assertThrows<ErrorIllegalState> {
            testInstance.numberGet(1)
        }.also { exception ->
            assertThat(
                exception.message, equalTo("Expected type to be number but was string")
            )
        }
    }

}
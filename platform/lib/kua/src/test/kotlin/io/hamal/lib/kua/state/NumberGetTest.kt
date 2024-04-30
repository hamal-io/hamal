package io.hamal.lib.kua.state

import io.hamal.lib.kua.StackTop
import io.hamal.lib.kua.numberGet
import io.hamal.lib.kua.type.KuaNumber
import io.hamal.lib.value.ValueString
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows

internal class NumberGetTest : StateBaseTest() {

    @TestFactory
    fun `Get value on stack without popping the value`() = runTest { testInstance ->
        testInstance.numberPush(KuaNumber(123))
        assertThat(testInstance.numberGet(1), equalTo(KuaNumber(123)))
        assertThat(testInstance.topGet(), equalTo(StackTop(1)))

        testInstance.numberPush(KuaNumber(234))
        assertThat(testInstance.numberGet(2), equalTo(KuaNumber(234)))
        assertThat(testInstance.topGet(), equalTo(StackTop(2)))
    }

    @TestFactory
    fun `Get value with negative index without popping the value`() = runTest { testInstance ->
        testInstance.numberPush(KuaNumber(123))
        assertThat(testInstance.numberGet(1), equalTo(KuaNumber(123)))
        assertThat(testInstance.topGet(), equalTo(StackTop(1)))

        testInstance.numberPush(KuaNumber(234))
        assertThat(testInstance.numberGet(-1), equalTo(KuaNumber(234)))
        assertThat(testInstance.topGet(), equalTo(StackTop(2)))
    }

    @TestFactory
    fun `Not a number`() = runTest { testInstance ->
        testInstance.stringPush(ValueString("Not  a boolean"))
        assertThrows<IllegalStateException> {
            testInstance.numberGet(1)
        }.also { exception ->
            assertThat(
                exception.message, equalTo("Expected type to be number but was string")
            )
        }
    }

}
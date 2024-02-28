package io.hamal.lib.kua.state

import io.hamal.lib.kua.StackTop
import io.hamal.lib.kua.type.KuaNumber
import io.hamal.lib.kua.type.KuaString
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows

internal class NumberGetTest : StateBaseTest() {

    @TestFactory
    fun `Reads value on stack without popping the value`() = runTest { testInstance ->
        testInstance.numberPush(KuaNumber(123))
        assertThat(testInstance.numberGet(1), equalTo(KuaNumber(123)))
        assertThat(testInstance.topGet(), equalTo(StackTop(1)))

        testInstance.numberPush(KuaNumber(234))
        assertThat(testInstance.numberGet(2), equalTo(KuaNumber(234)))
        assertThat(testInstance.topGet(), equalTo(StackTop(2)))
    }

    @TestFactory
    fun `Reads value on stack with negative index without popping the value`() = runTest { testInstance ->
        testInstance.numberPush(KuaNumber(123))
        assertThat(testInstance.numberGet(1), equalTo(KuaNumber(123)))
        assertThat(testInstance.topGet(), equalTo(StackTop(1)))

        testInstance.numberPush(KuaNumber(234))
        assertThat(testInstance.numberGet(-1), equalTo(KuaNumber(234)))
        assertThat(testInstance.topGet(), equalTo(StackTop(2)))
    }

    @TestFactory
    fun `Not a number`() = runTest { testInstance ->
        testInstance.stringPush(KuaString("Not  a boolean"))
        assertThrows<IllegalStateException> {
            testInstance.numberGet(1)
        }.also { exception ->
            assertThat(
                exception.message, equalTo("Expected type to be number but was string")
            )
        }
    }

}
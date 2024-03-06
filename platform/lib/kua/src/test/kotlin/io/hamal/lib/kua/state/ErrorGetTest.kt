package io.hamal.lib.kua.state

import io.hamal.lib.kua.StackTop
import io.hamal.lib.kua.errorGet
import io.hamal.lib.kua.type.KuaError
import io.hamal.lib.kua.type.KuaString
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows

internal class ErrorGetTest : StateBaseTest() {

    @TestFactory
    fun `Get value on stack without popping the value`() = runTest { testInstance ->
        testInstance.errorPush(KuaError("Some Error Message"))
        assertThat(testInstance.errorGet(1), equalTo(KuaError("Some Error Message")))
        assertThat(testInstance.topGet(), equalTo(StackTop(1)))

        testInstance.errorPush(KuaError("Another Error Message"))
        assertThat(testInstance.errorGet(2), equalTo(KuaError("Another Error Message")))
        assertThat(testInstance.topGet(), equalTo(StackTop(2)))
    }

    @TestFactory
    fun `Get value with negative index without popping the value`() = runTest { testInstance ->
        testInstance.errorPush(KuaError("Some Error Message"))
        assertThat(testInstance.errorGet(-1), equalTo(KuaError("Some Error Message")))
        assertThat(testInstance.topGet(), equalTo(StackTop(1)))

        testInstance.errorPush(KuaError("Another Error Message"))
        assertThat(testInstance.errorGet(-1), equalTo(KuaError("Another Error Message")))
        assertThat(testInstance.topGet(), equalTo(StackTop(2)))
    }

    @TestFactory
    fun `Not an error`() = runTest { testInstance ->
        testInstance.stringPush(KuaString("Not  a boolean"))
        assertThrows<IllegalStateException> {
            testInstance.errorGet(1)
        }.also { exception ->
            assertThat(
                exception.message,
                equalTo("Expected type to be error but was string")
            )
        }
    }

}
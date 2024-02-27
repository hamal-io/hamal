package io.hamal.lib.kua.state

import io.hamal.lib.kua.StackTop
import io.hamal.lib.kua.type.KuaError
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.TestFactory

internal class ErrorGetTest : StateBaseTest() {

    @TestFactory
    fun `Reads value on stack without popping the value`() = runTest { testInstance ->
        testInstance.errorPush(KuaError("Some Error Message"))
        assertThat(testInstance.errorGet(1), equalTo(KuaError("Some Error Message")))
        assertThat(testInstance.topGet(), equalTo(StackTop(1)))

        testInstance.errorPush(KuaError("Another Error Message"))
        assertThat(testInstance.errorGet(2), equalTo(KuaError("Another Error Message")))
        assertThat(testInstance.topGet(), equalTo(StackTop(2)))
    }

    @TestFactory
    fun `Reads value on stack with negative index without popping the value`() = runTest { testInstance ->
        testInstance.errorPush(KuaError("Some Error Message"))
        assertThat(testInstance.errorGet(-1), equalTo(KuaError("Some Error Message")))
        assertThat(testInstance.topGet(), equalTo(StackTop(1)))

        testInstance.errorPush(KuaError("Another Error Message"))
        assertThat(testInstance.errorGet(-1), equalTo(KuaError("Another Error Message")))
        assertThat(testInstance.topGet(), equalTo(StackTop(2)))
    }

}
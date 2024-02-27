package io.hamal.lib.kua.state

import io.hamal.lib.kua.StackTop
import io.hamal.lib.kua.type.KuaError
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.TestFactory

internal class ErrorPushTest : StateBaseTest() {
    @TestFactory
    fun `Pushes error value on stack`() = runTest { testInstance ->
        val result = testInstance.errorPush(KuaError("some error"))
        assertThat(result, equalTo(StackTop(1)))
        assertThat(testInstance.topGet(), equalTo(StackTop(1)))
        assertThat(testInstance.errorGet(1), equalTo(KuaError("some error")))
    }
}

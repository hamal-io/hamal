package io.hamal.lib.kua.state

import io.hamal.lib.kua.StackTop
import io.hamal.lib.kua.errorGet
import io.hamal.lib.common.value.ValueError
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.TestFactory

internal class ErrorPushTest : StateBaseTest() {
    @TestFactory
    fun `Pushes error value on stack`() = runTest { testInstance ->
        val result = testInstance.errorPush(ValueError("some error"))
        assertThat(result, equalTo(StackTop(1)))
        assertThat(testInstance.topGet(), equalTo(StackTop(1)))
        assertThat(testInstance.errorGet(1), equalTo(ValueError("some error")))
    }
}

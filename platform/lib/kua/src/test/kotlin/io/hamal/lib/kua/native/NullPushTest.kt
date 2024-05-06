package io.hamal.lib.kua.native

import io.hamal.lib.kua.ErrorIllegalArgument
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class NullPushTest : NativeBaseTest() {

    @Test
    fun `Pushes value on stack`() {
        val result = testInstance.nilPush()
        assertThat(result, equalTo(1))
        assertThat(testInstance.topGet(), equalTo(1))
    }

    @Test
    fun `Tries to push too many items on the stack limited to 999_999`() {
        repeat(999999) { testInstance.nilPush() }

        assertThrows<ErrorIllegalArgument> { testInstance.nilPush() }
            .also { exception -> assertThat(exception.message, equalTo("Prevented stack overflow")) }
    }
}
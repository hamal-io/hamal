package io.hamal.lib.kua.native

import io.hamal.lib.kua.ErrorIllegalArgument
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class StringPushTest : NativeBaseTest() {
    @Test
    fun `Pushes value on stack`() {
        val result = testInstance.stringPush("hamal")
        assertThat(result, equalTo(1))
        assertThat(testInstance.topGet(), equalTo(1))
        assertThat(testInstance.stringGet(1), equalTo("hamal"))
    }

    @Test
    fun `Tries to push too many items on the stack limited to 999_999`() {
        repeat(999999) { testInstance.stringPush("code-sleep-repeat") }

        assertThrows<ErrorIllegalArgument> { testInstance.stringPush("until you can not anymore") }
            .also { exception -> assertThat(exception.message, equalTo("Prevented stack overflow")) }
    }
}

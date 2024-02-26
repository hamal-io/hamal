package io.hamal.lib.kua.native

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class PushStringTest : NativeBaseTest() {
    @Test
    fun `Pushes value on stack`() {
        val result = testInstance.pushString("hamal")
        assertThat(result, equalTo(1))
        assertThat(testInstance.top(), equalTo(1))
        assertThat(testInstance.toString(1), equalTo("hamal"))
    }

    @Test
    fun `Tries to push too many items on the stack limited to 999_999`() {
        repeat(999999) { testInstance.pushString("code-sleep-repeat") }

        assertThrows<IllegalArgumentException> { testInstance.pushString("until you can not anymore") }
            .also { exception -> assertThat(exception.message, equalTo("Prevented stack overflow")) }
    }
}

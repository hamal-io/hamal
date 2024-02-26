package io.hamal.lib.kua.native

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class PushBooleanTest : NativeBaseTest() {

    @Test
    fun `Pushes value on stack`() {
        val result = testInstance.pushBoolean(true)
        assertThat(result, equalTo(1))
        assertThat(testInstance.top(), equalTo(1))
        assertThat(testInstance.toBoolean(1), equalTo(true))
    }

    @Test
    fun `Tries to push too many items on the stack limited to 999_999`() {
        repeat(999999) { testInstance.pushBoolean(true) }

        assertThrows<IllegalArgumentException> { testInstance.pushBoolean(true) }
            .also { exception -> assertThat(exception.message, equalTo("Prevented stack overflow")) }
    }
}

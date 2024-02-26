package io.hamal.lib.kua.native

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class PushNilTest : NativeBaseTest() {

    @Test
    fun `Pushes value on stack`() {
        val result = testInstance.pushNil()
        assertThat(result, equalTo(1))
        assertThat(testInstance.top(), equalTo(1))
    }

    @Test
    fun `Tries to push too many items on the stack limited to 999_999`() {
        repeat(999999) { testInstance.pushNil() }

        assertThrows<IllegalArgumentException> { testInstance.pushNil() }
            .also { exception -> assertThat(exception.message, equalTo("Prevented stack overflow")) }
    }
}
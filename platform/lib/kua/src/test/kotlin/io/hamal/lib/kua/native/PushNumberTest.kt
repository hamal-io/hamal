package io.hamal.lib.kua.native

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class PushNumberTest : NativeBaseTest() {

    @Test
    fun `Pushes value on stack`() {
        val result = testInstance.pushNumber(13.37)
        assertThat(result, equalTo(1))
        assertThat(testInstance.top(), equalTo(1))
        assertThat(testInstance.toNumber(1), equalTo(13.37))
    }

    @Test
    fun `Tries to push too many items on the stack limited to 999_999`() {
        repeat(999999) { testInstance.pushNumber(it.toDouble()) }
        assertThrows<IllegalArgumentException> { testInstance.pushNumber(-1.0) }
            .also { exception -> assertThat(exception.message, equalTo("Prevented stack overflow")) }
    }
}
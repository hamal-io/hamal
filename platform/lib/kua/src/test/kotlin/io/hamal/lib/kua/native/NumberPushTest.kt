package io.hamal.lib.kua.native

import io.hamal.lib.kua.ErrorIllegalArgument
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class NumberPushTest : NativeBaseTest() {

    @Test
    fun `Pushes value on stack`() {
        val result = testInstance.numberPush(13.37)
        assertThat(result, equalTo(1))
        assertThat(testInstance.topGet(), equalTo(1))
        assertThat(testInstance.numberGet(1), equalTo(13.37))
    }

    @Test
    fun `Tries to push too many items on the stack limited to 999_999`() {
        repeat(999999) { testInstance.numberPush(it.toDouble()) }
        assertThrows<ErrorIllegalArgument> { testInstance.numberPush(-1.0) }
            .also { exception -> assertThat(exception.message, equalTo("Prevented stack overflow")) }
    }
}
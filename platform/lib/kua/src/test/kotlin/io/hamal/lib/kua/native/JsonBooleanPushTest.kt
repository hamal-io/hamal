package io.hamal.lib.kua.native

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class JsonBooleanPushTest : NativeBaseTest() {

    @Test
    fun `Pushes value on stack`() {
        val result = testInstance.booleanPush(true)
        assertThat(result, equalTo(1))
        assertThat(testInstance.topGet(), equalTo(1))
        assertThat(testInstance.booleanGet(1), equalTo(true))
    }

    @Test
    fun `Tries to push too many items on the stack limited to 999_999`() {
        repeat(999999) { testInstance.booleanPush(true) }

        assertThrows<IllegalArgumentException> { testInstance.booleanPush(true) }
            .also { exception -> assertThat(exception.message, equalTo("Prevented stack overflow")) }
    }
}

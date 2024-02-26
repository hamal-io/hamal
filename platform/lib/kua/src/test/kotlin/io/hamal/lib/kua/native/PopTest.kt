package io.hamal.lib.kua.native

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class PopTest : NativeBaseTest() {

    @Test
    fun `Tries to pop negative amount from empty`() {
        assertThrows<IllegalArgumentException> { testInstance.pop(-1) }
            .also { exception ->
                assertThat(exception.message, equalTo("Total must be positive (>0)"))
            }
    }

    @Test
    fun `Tries to pop -1 elements from empty stack`() {
        testInstance.pushBoolean(true)
        assertThrows<IllegalArgumentException> { testInstance.pop(0) }
            .also { exception -> assertThat(exception.message, equalTo("Total must be positive (>0)")) }
    }

    @Test
    fun `Tries to pop 1 element from empty stack`() {
        assertThrows<IllegalArgumentException> { testInstance.pop(1) }
            .also { exception -> assertThat(exception.message, equalTo("Prevented stack underflow")) }
    }

    @Test
    fun `Pops 2 elements from stack`() {
        testInstance.pushNumber(1.0)
        testInstance.pushNumber(2.0)
        testInstance.pushNumber(3.0)

        val result = testInstance.pop(2)
        assertThat(result, equalTo(1))
        assertThat(testInstance.top(), equalTo(1))

        assertThat(testInstance.toNumber(1), equalTo(1.0))
    }
}

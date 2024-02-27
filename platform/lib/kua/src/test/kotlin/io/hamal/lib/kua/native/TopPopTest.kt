package io.hamal.lib.kua.native

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class TopPopTest : NativeBaseTest() {
    @Test
    fun `Pops 2 elements from stack`() {
        testInstance.numberPush(1.0)
        testInstance.numberPush(2.0)
        testInstance.numberPush(3.0)

        val result = testInstance.topPop(2)
        assertThat(result, equalTo(1))
        assertThat(testInstance.topGet(), equalTo(1))

        assertThat(testInstance.numberGet(1), equalTo(1.0))
    }

    @Test
    fun `Tries to pop negative amount from empty`() {
        assertThrows<IllegalArgumentException> { testInstance.topPop(-1) }
            .also { exception ->
                assertThat(exception.message, equalTo("Total must be positive (>0)"))
            }
    }

    @Test
    fun `Tries to pop -1 elements from empty stack`() {
        testInstance.booleanPush(true)
        assertThrows<IllegalArgumentException> { testInstance.topPop(0) }
            .also { exception -> assertThat(exception.message, equalTo("Total must be positive (>0)")) }
    }

    @Test
    fun `Tries to pop 1 element from empty stack`() {
        assertThrows<IllegalArgumentException> { testInstance.topPop(1) }
            .also { exception -> assertThat(exception.message, equalTo("Prevented stack underflow")) }
    }
}

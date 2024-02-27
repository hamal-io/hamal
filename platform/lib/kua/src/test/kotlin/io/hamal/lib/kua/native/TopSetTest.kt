package io.hamal.lib.kua.native

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class TopSetTest : NativeBaseTest() {

    @Test
    fun `Can grows an empty stack`() {
        testInstance.topSet(10)
        assertThat(testInstance.topGet(), equalTo(10))

        repeat(10) { idx ->
            assertThat(testInstance.type(idx + 1), equalTo(0))
        }
    }

    @Test
    fun `Can shrink a stack`() {
        testInstance.numberPush(1.0)
        testInstance.numberPush(2.0)
        testInstance.numberPush(3.0)
        testInstance.numberPush(4.0)

        testInstance.topSet(-3)
        assertThat(testInstance.topGet(), equalTo(2))
        assertThat(testInstance.numberGet(1), equalTo(1.0))
        assertThat(testInstance.numberGet(2), equalTo(2.0))
    }

    @Test
    fun `Tries to shrink the stack to be negative`() {
        assertThrows<IllegalArgumentException> { testInstance.topSet(-10) }
            .also { exception -> assertThat(exception.message, equalTo("Prevented stack underflow")) }
    }


    @Test
    fun `Tries to set stack size bigger than max stack size`() {
        assertThrows<IllegalArgumentException> { testInstance.topSet(1000000) }
            .also { exception -> assertThat(exception.message, equalTo("Prevented stack overflow")) }
    }

    @Test
    fun `Tries to set negative stack size bigger than max stack size`() {
        assertThrows<IllegalArgumentException> { testInstance.topSet(-1000000) }
            .also { exception -> assertThat(exception.message, equalTo("Prevented stack underflow")) }
    }

}
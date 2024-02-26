package io.hamal.lib.kua.native

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class ToNumberTest : NativeBaseTest() {
    @Test
    fun `Tries to read Number with 0 index`() {
        testInstance.pushNumber(812.123)
        assertThrows<IllegalArgumentException> { testInstance.toNumber(0) }
            .also { exception -> assertThat(exception.message, equalTo("Index must not be 0")) }
    }

    @Test
    fun `Tries to read Number with index bigger than stack size`() {
        testInstance.pushNumber(123.321)
        assertThrows<IllegalArgumentException> { testInstance.toNumber(2) }
            .also { exception -> assertThat(exception.message, equalTo("Index out of bounds")) }
    }

    @Test
    fun `Tries to read Number with abs(negative index) bigger than stack size`() {
        testInstance.pushNumber(123.321)
        assertThrows<IllegalArgumentException> { testInstance.toNumber(-2) }
            .also { exception -> assertThat(exception.message, equalTo("Index out of bounds")) }
    }


    @Test
    fun `Tries to read not a number as number`() {
        testInstance.pushBoolean(true)
        assertThrows<IllegalStateException> { testInstance.toNumber(1) }
            .also { exception -> assertThat(exception.message, equalTo("Expected type to be number but was boolean")) }
    }

    @Test
    fun `Reads value on stack without popping the value`() {
        testInstance.pushNumber(99.88)
        assertThat(testInstance.toNumber(1), equalTo(99.88))
        assertThat(testInstance.top(), equalTo(1))

        testInstance.pushNumber(88.77)
        assertThat(testInstance.toNumber(2), equalTo(88.77))
        assertThat(testInstance.top(), equalTo(2))
    }

    @Test
    fun `Reads value on stack with negative index without popping the value`() {
        testInstance.pushNumber(99.88)
        assertThat(testInstance.toNumber(-1), equalTo(99.88))
        assertThat(testInstance.top(), equalTo(1))

        testInstance.pushNumber(88.77)
        assertThat(testInstance.toNumber(-1), equalTo(88.77))
        assertThat(testInstance.top(), equalTo(2))
    }
}
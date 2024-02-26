package io.hamal.lib.kua.native

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class ToBooleanTest : NativeBaseTest() {

    @Test
    fun `Tries to read boolean with 0 index`() {
        testInstance.pushBoolean(true)
        assertThrows<IllegalArgumentException> { testInstance.toBoolean(0) }
            .also { exception -> assertThat(exception.message, equalTo("Index must not be 0")) }
    }

    @Test
    fun `Tries to read boolean with index bigger than stack size`() {
        testInstance.pushBoolean(true)
        assertThrows<IllegalArgumentException> { testInstance.toBoolean(2) }
            .also { exception -> assertThat(exception.message, equalTo("Index out of bounds")) }
    }

    @Test
    fun `Tries to read boolean with abs(negative index) bigger than stack size`() {
        testInstance.pushBoolean(true)
        assertThrows<IllegalArgumentException> { testInstance.toBoolean(-2) }
            .also { exception -> assertThat(exception.message, equalTo("Index out of bounds")) }
    }

    @Test
    fun `Tries to read not boolean as as boolean`() {
        testInstance.pushNumber(1.0)
        assertThrows<IllegalStateException> { testInstance.toBoolean(1) }
            .also { exception -> assertThat(exception.message, equalTo("Expected type to be boolean but was number")) }

    }

    @Test
    fun `Reads value on stack without popping the value`() {
        testInstance.pushBoolean(true)
        assertThat(testInstance.toBoolean(1), equalTo(true))
        assertThat(testInstance.top(), equalTo(1))

        testInstance.pushBoolean(false)
        assertThat(testInstance.toBoolean(2), equalTo(false))
        assertThat(testInstance.top(), equalTo(2))
    }

    @Test
    fun `Reads value on stack with negative index without popping the value`() {
        testInstance.pushBoolean(true)
        assertThat(testInstance.toBoolean(-1), equalTo(true))
        assertThat(testInstance.top(), equalTo(1))

        testInstance.pushBoolean(false)
        assertThat(testInstance.toBoolean(-1), equalTo(false))
        assertThat(testInstance.top(), equalTo(2))
    }
}

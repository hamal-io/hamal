package io.hamal.lib.kua.native

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class ToStringTest : NativeBaseTest() {

    @Test
    fun `Tries to read String with 0 index`() {
        testInstance.pushString("some-string")
        assertThrows<IllegalArgumentException> { testInstance.toString(0) }
            .also { exception -> assertThat(exception.message, equalTo("Index must not be 0")) }
    }

    @Test
    fun `Tries to read String with index bigger than stack size`() {
        testInstance.pushString("some-string")
        assertThrows<IllegalArgumentException> { testInstance.toString(2) }
            .also { exception -> assertThat(exception.message, equalTo("Index out of bounds")) }
    }

    @Test
    fun `Tries to read String with abs(negative index) bigger than stack size`() {
        testInstance.pushString("some-string")
        assertThrows<IllegalArgumentException> { testInstance.toString(2) }
            .also { exception -> assertThat(exception.message, equalTo("Index out of bounds")) }
    }

    @Test
    fun `Tries to read not a string as string`() {
        testInstance.pushNumber(1.0)
        assertThrows<IllegalStateException> { testInstance.toString(1) }
            .also { exception -> assertThat(exception.message, equalTo("Expected type to be string but was number")) }
    }

    @Test
    fun `Reads value on stack without popping the value`() {
        testInstance.pushString("eat-poop-sleep-repeat")
        assertThat(testInstance.toString(1), equalTo("eat-poop-sleep-repeat"))
        assertThat(testInstance.top(), equalTo(1))

        testInstance.pushString("or-write-some-code")
        assertThat(testInstance.toString(2), equalTo("or-write-some-code"))
        assertThat(testInstance.top(), equalTo(2))
    }

    @Test
    fun `Reads value on stack with negative index without popping the value`() {
        testInstance.pushString("eat-poop-sleep-repeat")
        assertThat(testInstance.toString(-1), equalTo("eat-poop-sleep-repeat"))
        assertThat(testInstance.top(), equalTo(1))

        testInstance.pushString("or-write-some-code")
        assertThat(testInstance.toString(-1), equalTo("or-write-some-code"))
        assertThat(testInstance.top(), equalTo(2))
    }
}
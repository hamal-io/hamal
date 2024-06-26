package io.hamal.lib.kua.native

import io.hamal.lib.kua.ErrorIllegalArgument
import io.hamal.lib.kua.ErrorIllegalState
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class StringGetTest : NativeBaseTest() {

    @Test
    fun `Get value on stack without popping the value`() {
        testInstance.stringPush("eat-poop-sleep-repeat")
        assertThat(testInstance.stringGet(1), equalTo("eat-poop-sleep-repeat"))
        assertThat(testInstance.topGet(), equalTo(1))

        testInstance.stringPush("or-write-some-code")
        assertThat(testInstance.stringGet(2), equalTo("or-write-some-code"))
        assertThat(testInstance.topGet(), equalTo(2))
    }

    @Test
    fun `Get value with negative index without popping the value`() {
        testInstance.stringPush("eat-poop-sleep-repeat")
        assertThat(testInstance.stringGet(-1), equalTo("eat-poop-sleep-repeat"))
        assertThat(testInstance.topGet(), equalTo(1))

        testInstance.stringPush("or-write-some-code")
        assertThat(testInstance.stringGet(-1), equalTo("or-write-some-code"))
        assertThat(testInstance.topGet(), equalTo(2))
    }


    @Test
    fun `Tries to get string with 0 index`() {
        testInstance.stringPush("some-string")
        assertThrows<ErrorIllegalArgument> { testInstance.stringGet(0) }
            .also { exception -> assertThat(exception.message, equalTo("Index must not be 0")) }
    }

    @Test
    fun `Tries to get string with index bigger than stack size`() {
        testInstance.stringPush("some-string")
        assertThrows<ErrorIllegalArgument> { testInstance.stringGet(2) }
            .also { exception -> assertThat(exception.message, equalTo("Index out of bounds")) }
    }

    @Test
    fun `Tries to get string with abs(negative index) bigger than stack size`() {
        testInstance.stringPush("some-string")
        assertThrows<ErrorIllegalArgument> { testInstance.stringGet(2) }
            .also { exception -> assertThat(exception.message, equalTo("Index out of bounds")) }
    }

    @Test
    fun `Tries to get not a string as string`() {
        testInstance.numberPush(1.0)
        assertThrows<ErrorIllegalState> { testInstance.stringGet(1) }
            .also { exception -> assertThat(exception.message, equalTo("Expected type to be string but was number")) }
    }
}
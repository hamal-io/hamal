package io.hamal.lib.kua.native

import io.hamal.lib.kua.ErrorIllegalArgument
import io.hamal.lib.kua.ErrorIllegalState
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class ErrorGetTest : NativeBaseTest() {

    @Test
    fun `Tries to get error with 0 index`() {
        testInstance.errorPush("Error Message")
        assertThrows<ErrorIllegalArgument> { testInstance.errorGet(0) }
            .also { exception -> assertThat(exception.message, equalTo("Index must not be 0")) }
    }

    @Test
    fun `Tries to get error with index bigger than stack size`() {
        testInstance.errorPush("Error Message")
        assertThrows<ErrorIllegalArgument> { testInstance.errorGet(2) }
            .also { exception -> assertThat(exception.message, equalTo("Index out of bounds")) }
    }

    @Test
    fun `Tries to get error with abs(negative index) bigger than stack size`() {
        testInstance.errorPush("Error Message")
        assertThrows<ErrorIllegalArgument> { testInstance.errorGet(-2) }
            .also { exception -> assertThat(exception.message, equalTo("Index out of bounds")) }
    }

    @Test
    fun `Tries to get not an error as error`() {
        testInstance.numberPush(1.0)
        assertThrows<ErrorIllegalState> { testInstance.errorGet(1) }
            .also { exception ->
                assertThat(exception.message, equalTo("Expected type to be error but was number"))
            }
    }

    @Test
    fun `Get value on stack without popping the value`() {
        testInstance.errorPush("Some Error Message")
        assertThat(testInstance.errorGet(1), equalTo("Some Error Message"))
        assertThat(testInstance.topGet(), equalTo(1))

        testInstance.errorPush("Another Error Message")
        assertThat(testInstance.errorGet(2), equalTo("Another Error Message"))
        assertThat(testInstance.topGet(), equalTo(2))
    }

    @Test
    fun `Get value with negative index without popping the value`() {
        testInstance.errorPush("Some Error Message")
        assertThat(testInstance.errorGet(-1), equalTo("Some Error Message"))
        assertThat(testInstance.topGet(), equalTo(1))

        testInstance.errorPush("Another Error Message")
        assertThat(testInstance.errorGet(-1), equalTo("Another Error Message"))
        assertThat(testInstance.topGet(), equalTo(2))
    }

}
package io.hamal.lib.kua.native

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class SerdeBooleanGetTest : NativeBaseTest() {

    @Test
    fun `Tries to get boolean with 0 index`() {
        testInstance.booleanPush(true)
        assertThrows<IllegalArgumentException> { testInstance.booleanGet(0) }
            .also { exception -> assertThat(exception.message, equalTo("Index must not be 0")) }
    }

    @Test
    fun `Tries to get boolean with index bigger than stack size`() {
        testInstance.booleanPush(true)
        assertThrows<IllegalArgumentException> { testInstance.booleanGet(2) }
            .also { exception -> assertThat(exception.message, equalTo("Index out of bounds")) }
    }

    @Test
    fun `Tries to get boolean with abs(negative index) bigger than stack size`() {
        testInstance.booleanPush(true)
        assertThrows<IllegalArgumentException> { testInstance.booleanGet(-2) }
            .also { exception -> assertThat(exception.message, equalTo("Index out of bounds")) }
    }

    @Test
    fun `Tries to get not boolean as as boolean`() {
        testInstance.numberPush(1.0)
        assertThrows<IllegalStateException> { testInstance.booleanGet(1) }
            .also { exception -> assertThat(exception.message, equalTo("Expected type to be boolean but was number")) }

    }

    @Test
    fun `Get value on stack without popping the value`() {
        testInstance.booleanPush(true)
        assertThat(testInstance.booleanGet(1), equalTo(true))
        assertThat(testInstance.topGet(), equalTo(1))

        testInstance.booleanPush(false)
        assertThat(testInstance.booleanGet(2), equalTo(false))
        assertThat(testInstance.topGet(), equalTo(2))
    }

    @Test
    fun `Get value with negative index without popping the value`() {
        testInstance.booleanPush(true)
        assertThat(testInstance.booleanGet(-1), equalTo(true))
        assertThat(testInstance.topGet(), equalTo(1))

        testInstance.booleanPush(false)
        assertThat(testInstance.booleanGet(-1), equalTo(false))
        assertThat(testInstance.topGet(), equalTo(2))
    }
}

package io.hamal.lib.kua.native

import io.hamal.lib.kua.ErrorIllegalArgument
import io.hamal.lib.kua.ErrorIllegalState
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class NumberGetTest : NativeBaseTest() {
    @Test
    fun `Get value on stack without popping the value`() {
        testInstance.numberPush(99.88)
        assertThat(testInstance.numberGet(1), equalTo(99.88))
        assertThat(testInstance.topGet(), equalTo(1))

        testInstance.numberPush(88.77)
        assertThat(testInstance.numberGet(2), equalTo(88.77))
        assertThat(testInstance.topGet(), equalTo(2))
    }

    @Test
    fun `Get value with negative index without popping the value`() {
        testInstance.numberPush(99.88)
        assertThat(testInstance.numberGet(-1), equalTo(99.88))
        assertThat(testInstance.topGet(), equalTo(1))

        testInstance.numberPush(88.77)
        assertThat(testInstance.numberGet(-1), equalTo(88.77))
        assertThat(testInstance.topGet(), equalTo(2))
    }

    @Test
    fun `Tries to get number with 0 index`() {
        testInstance.numberPush(812.123)
        assertThrows<ErrorIllegalArgument> { testInstance.numberGet(0) }
            .also { exception -> assertThat(exception.message, equalTo("Index must not be 0")) }
    }

    @Test
    fun `Tries to get number with index bigger than stack size`() {
        testInstance.numberPush(123.321)
        assertThrows<ErrorIllegalArgument> { testInstance.numberGet(2) }
            .also { exception -> assertThat(exception.message, equalTo("Index out of bounds")) }
    }

    @Test
    fun `Tries to get number with abs(negative index) bigger than stack size`() {
        testInstance.numberPush(123.321)
        assertThrows<ErrorIllegalArgument> { testInstance.numberGet(-2) }
            .also { exception -> assertThat(exception.message, equalTo("Index out of bounds")) }
    }


    @Test
    fun `Tries to get not a number as number`() {
        testInstance.booleanPush(true)
        assertThrows<ErrorIllegalState> { testInstance.numberGet(1) }
            .also { exception -> assertThat(exception.message, equalTo("Expected type to be number but was boolean")) }
    }

}
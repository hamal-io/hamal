package io.hamal.lib.kua.native

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class DecimalGetTest : NativeBaseTest() {

    @Test
    fun `Tries to get decimal with 0 index`() {
        testInstance.decimalPush("123")
        assertThrows<IllegalArgumentException> { testInstance.decimalGet(0) }
            .also { exception -> assertThat(exception.message, equalTo("Index must not be 0")) }
    }

    @Test
    fun `Tries to get decimal with index bigger than stack size`() {
        testInstance.decimalPush("123")
        assertThrows<IllegalArgumentException> { testInstance.booleanGet(2) }
            .also { exception -> assertThat(exception.message, equalTo("Index out of bounds")) }
    }

    @Test
    fun `Tries to get decimal with abs(negative index) bigger than stack size`() {
        testInstance.decimalPush("123")
        assertThrows<IllegalArgumentException> { testInstance.booleanGet(-2) }
            .also { exception -> assertThat(exception.message, equalTo("Index out of bounds")) }
    }

    @Test
    fun `Tries to get not decimal as decimal`() {
        testInstance.numberPush(1.0)
        assertThrows<IllegalStateException> { testInstance.decimalGet(1) }
            .also { exception -> assertThat(exception.message, equalTo("Expected type to be decimal but was number")) }
    }

    @Test
    fun `Get value on stack without popping the value`() {
        testInstance.decimalPush("123")
        assertThat(testInstance.decimalGet(1), equalTo("123"))
        assertThat(testInstance.topGet(), equalTo(1))

        testInstance.decimalPush("234")
        assertThat(testInstance.decimalGet(2), equalTo("234"))
        assertThat(testInstance.topGet(), equalTo(2))
    }

    @Test
    fun `Get value with negative index without popping the value`() {
        testInstance.decimalPush("123")
        assertThat(testInstance.decimalGet(-1), equalTo("123"))
        assertThat(testInstance.topGet(), equalTo(1))

        testInstance.decimalPush("234")
        assertThat(testInstance.decimalGet(-1), equalTo("234"))
        assertThat(testInstance.topGet(), equalTo(2))
    }
}
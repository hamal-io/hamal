package io.hamal.lib.kua.native

import io.hamal.lib.kua.type.KuaDecimal
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class DecimalGetTest : NativeBaseTest() {

    @Test
    fun `Tries to read decimal with 0 index`() {
        testInstance.decimalPush(KuaDecimal(123))
        assertThrows<IllegalArgumentException> { testInstance.decimalGet(0) }
            .also { exception -> assertThat(exception.message, equalTo("Index must not be 0")) }
    }

    @Test
    fun `Tries to read decimal with index bigger than stack size`() {
        testInstance.decimalPush(KuaDecimal(123))
        assertThrows<IllegalArgumentException> { testInstance.booleanGet(2) }
            .also { exception -> assertThat(exception.message, equalTo("Index out of bounds")) }
    }

    @Test
    fun `Tries to read decimal with abs(negative index) bigger than stack size`() {
        testInstance.decimalPush(KuaDecimal(123))
        assertThrows<IllegalArgumentException> { testInstance.booleanGet(-2) }
            .also { exception -> assertThat(exception.message, equalTo("Index out of bounds")) }
    }

    @Test
    fun `Tries to read not decimal as decimal`() {
        testInstance.numberPush(1.0)
        assertThrows<IllegalStateException> { testInstance.decimalGet(1) }
            .also { exception -> assertThat(exception.message, equalTo("Expected type to be decimal but was number")) }
    }

    @Test
    fun `Reads value on stack without popping the value`() {
        testInstance.decimalPush(KuaDecimal(123))
        assertThat(testInstance.decimalGet(1), equalTo(KuaDecimal(123)))
        assertThat(testInstance.topGet(), equalTo(1))

        testInstance.decimalPush(KuaDecimal(234))
        assertThat(testInstance.decimalGet(2), equalTo(KuaDecimal(234)))
        assertThat(testInstance.topGet(), equalTo(2))
    }

    @Test
    fun `Reads value on stack with negative index without popping the value`() {
        testInstance.decimalPush(KuaDecimal(123))
        assertThat(testInstance.decimalGet(-1), equalTo(KuaDecimal(123)))
        assertThat(testInstance.topGet(), equalTo(1))

        testInstance.decimalPush(KuaDecimal(234))
        assertThat(testInstance.decimalGet(-1), equalTo(KuaDecimal(234)))
        assertThat(testInstance.topGet(), equalTo(2))
    }
}
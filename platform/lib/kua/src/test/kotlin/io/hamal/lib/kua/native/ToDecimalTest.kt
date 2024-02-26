package io.hamal.lib.kua.native

import io.hamal.lib.kua.type.KuaDecimal
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class ToDecimalTest : NativeBaseTest() {

    @Test
    fun `Tries to read decimal with 0 index`() {
        testInstance.pushDecimal(KuaDecimal(123))
        assertThrows<IllegalArgumentException> { testInstance.toDecimal(0) }
            .also { exception -> assertThat(exception.message, equalTo("Index must not be 0")) }
    }

    @Test
    fun `Tries to read decimal with index bigger than stack size`() {
        testInstance.pushDecimal(KuaDecimal(123))
        assertThrows<IllegalArgumentException> { testInstance.toBoolean(2) }
            .also { exception -> assertThat(exception.message, equalTo("Index out of bounds")) }
    }

    @Test
    fun `Tries to read decimal with abs(negative index) bigger than stack size`() {
        testInstance.pushDecimal(KuaDecimal(123))
        assertThrows<IllegalArgumentException> { testInstance.toBoolean(-2) }
            .also { exception -> assertThat(exception.message, equalTo("Index out of bounds")) }
    }

    @Test
    fun `Tries to read not decimal as decimal`() {
        testInstance.pushNumber(1.0)
        assertThrows<IllegalStateException> { testInstance.toDecimal(1) }
            .also { exception -> assertThat(exception.message, equalTo("Expected type to be decimal but was number")) }
    }

    @Test
    fun `Reads value on stack without popping the value`() {
        testInstance.pushDecimal(KuaDecimal(123))
        assertThat(testInstance.toDecimal(1), equalTo(KuaDecimal(123)))
        assertThat(testInstance.top(), equalTo(1))

        testInstance.pushDecimal(KuaDecimal(234))
        assertThat(testInstance.toDecimal(2), equalTo(KuaDecimal(234)))
        assertThat(testInstance.top(), equalTo(2))
    }

    @Test
    fun `Reads value on stack with negative index without popping the value`() {
        testInstance.pushDecimal(KuaDecimal(123))
        assertThat(testInstance.toDecimal(-1), equalTo(KuaDecimal(123)))
        assertThat(testInstance.top(), equalTo(1))

        testInstance.pushDecimal(KuaDecimal(234))
        assertThat(testInstance.toDecimal(-1), equalTo(KuaDecimal(234)))
        assertThat(testInstance.top(), equalTo(2))
    }
}
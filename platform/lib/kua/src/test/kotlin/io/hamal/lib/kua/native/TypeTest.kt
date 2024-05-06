package io.hamal.lib.kua.native

import io.hamal.lib.kua.ErrorIllegalArgument
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class TypeTest : NativeBaseTest() {

    @Test
    fun `Boolean`() {
        testInstance.booleanPush(true)
        val result = testInstance.type(1)
        assertThat(result, equalTo(1))
    }

    @Test
    fun `Decimal`() {
        testInstance.decimalPush("23.456")
        assertThat(testInstance.type(1), equalTo(11))
    }

    @Test
    fun `Error`() {
        testInstance.errorPush("error message")
        assertThat(testInstance.type(1), equalTo(10))
    }

    @Test
    fun `Function`() {
        testInstance.stringLoad("local x = 10")
        assertThat(testInstance.type(1), equalTo(6))
    }

    @Test
    fun `Nil`() {
        testInstance.nilPush()
        val result = testInstance.type(1)
        assertThat(result, equalTo(0))
    }

    @Test
    fun `Number`() {
        testInstance.numberPush(13.0)
        val result = testInstance.type(1)
        assertThat(result, equalTo(3))
    }

    @Test
    fun `String`() {
        testInstance.stringPush("hamal")
        val result = testInstance.type(1)
        assertThat(result, equalTo(4))
    }

    @Test
    fun `Table`() {
        testInstance.tableCreate(0, 0)
        val result = testInstance.type(1)
        assertThat(result, equalTo(5))
    }

    @Test
    fun `Tries to get boolean with 0 index`() {
        testInstance.booleanPush(true)
        assertThrows<ErrorIllegalArgument> { testInstance.type(0) }
            .also { exception -> assertThat(exception.message, equalTo("Index must not be 0")) }
    }

    @Test
    fun `Tries to get type with index bigger than stack size`() {
        testInstance.booleanPush(true)
        assertThrows<ErrorIllegalArgument> { testInstance.type(2) }
            .also { exception -> assertThat(exception.message, equalTo("Index out of bounds")) }
    }

    @Test
    fun `Tries to get type with abs(negative index) bigger than stack size`() {
        testInstance.booleanPush(true)
        assertThrows<ErrorIllegalArgument> { testInstance.type(-2) }
            .also { exception -> assertThat(exception.message, equalTo("Index out of bounds")) }
    }
}

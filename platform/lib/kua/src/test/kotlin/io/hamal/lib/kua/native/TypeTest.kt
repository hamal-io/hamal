package io.hamal.lib.kua.native

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class TypeTest : NativeBaseTest() {

    @Test
    fun `Tries to read boolean with 0 index`() {
        testInstance.booleanPush(true)
        assertThrows<IllegalArgumentException> { testInstance.type(0) }
            .also { exception -> assertThat(exception.message, equalTo("Index must not be 0")) }
    }

    @Test
    fun `Tries to get type with index bigger than stack size`() {
        testInstance.booleanPush(true)
        assertThrows<IllegalArgumentException> { testInstance.type(2) }
            .also { exception -> assertThat(exception.message, equalTo("Index out of bounds")) }
    }

    @Test
    fun `Tries to get type with abs(negative index) bigger than stack size`() {
        testInstance.booleanPush(true)
        assertThrows<IllegalArgumentException> { testInstance.type(-2) }
            .also { exception -> assertThat(exception.message, equalTo("Index out of bounds")) }
    }

    @Test
    fun `Nil`() {
        testInstance.nilPush()
        val result = testInstance.type(1)
        assertThat(result, equalTo(0))
    }

    @Test
    fun `Boolean`() {
        testInstance.booleanPush(true)
        val result = testInstance.type(1)
        assertThat(result, equalTo(1))
    }

    @Test
    @Disabled
    fun `LightUserData`() {
        TODO()
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
    fun `Function`() {
        testInstance.stringLoad("local x = 10")
        assertThat(testInstance.type(1), equalTo(6))
    }

    @Test
    @Disabled
    fun `UserData`() {
        TODO()
    }

    @Test
    @Disabled
    fun `Thread`() {
        TODO()
    }

    @Test
    fun `Error`() {
        testInstance.errorPush("error message")
        assertThat(testInstance.type(1), equalTo(10))
    }

    @Test
    fun `DecimalType`() {
        testInstance.decimalPush("23.456")
        assertThat(testInstance.type(1), equalTo(11))
    }
}

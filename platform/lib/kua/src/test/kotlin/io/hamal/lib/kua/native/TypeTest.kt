package io.hamal.lib.kua.native

import io.hamal.lib.kua.type.KuaDecimal
import io.hamal.lib.kua.type.KuaError
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class TypeTest : NativeBaseTest() {

    @Test
    fun `Tries to read boolean with 0 index`() {
        testInstance.pushBoolean(true)
        assertThrows<IllegalArgumentException> { testInstance.type(0) }
            .also { exception -> assertThat(exception.message, equalTo("Index must not be 0")) }
    }

    @Test
    fun `Tries to get type with index bigger than stack size`() {
        testInstance.pushBoolean(true)
        assertThrows<IllegalArgumentException> { testInstance.type(2) }
            .also { exception -> assertThat(exception.message, equalTo("Index out of bounds")) }
    }

    @Test
    fun `Tries to get type with abs(negative index) bigger than stack size`() {
        testInstance.pushBoolean(true)
        assertThrows<IllegalArgumentException> { testInstance.type(-2) }
            .also { exception -> assertThat(exception.message, equalTo("Index out of bounds")) }
    }

    @Test
    fun `Nil`() {
        testInstance.pushNil()
        val result = testInstance.type(1)
        assertThat(result, equalTo(0))
    }

    @Test
    fun `Boolean`() {
        testInstance.pushBoolean(true)
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
        testInstance.pushNumber(13.0)
        val result = testInstance.type(1)
        assertThat(result, equalTo(3))
    }

    @Test
    fun `String`() {
        testInstance.pushString("hamal")
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
        testInstance.loadString("local x = 10")
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
        testInstance.pushError(KuaError("error message"))
        assertThat(testInstance.type(1), equalTo(10))
    }

    @Test
    fun `DecimalType`() {
        testInstance.pushDecimal(KuaDecimal("23.456"))
        assertThat(testInstance.type(1), equalTo(11))
    }
}

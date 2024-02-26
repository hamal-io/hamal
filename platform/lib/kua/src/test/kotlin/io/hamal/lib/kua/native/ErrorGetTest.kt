package io.hamal.lib.kua.native

import io.hamal.lib.kua.type.KuaError
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class ErrorGetTest : NativeBaseTest() {

    @Test
    fun `Tries to read error with 0 index`() {
        testInstance.errorPush(KuaError("Error Message"))
        assertThrows<IllegalArgumentException> { testInstance.errorGet(0) }
            .also { exception -> assertThat(exception.message, equalTo("Index must not be 0")) }
    }

    @Test
    fun `Tries to read error with index bigger than stack size`() {
        testInstance.errorPush(KuaError("Error Message"))
        assertThrows<IllegalArgumentException> { testInstance.errorGet(2) }
            .also { exception -> assertThat(exception.message, equalTo("Index out of bounds")) }
    }

    @Test
    fun `Tries to read error with abs(negative index) bigger than stack size`() {
        testInstance.errorPush(KuaError("Error Message"))
        assertThrows<IllegalArgumentException> { testInstance.errorGet(-2) }
            .also { exception -> assertThat(exception.message, equalTo("Index out of bounds")) }
    }

    @Test
    fun `Tries to read not an error as error`() {
        testInstance.numberPush(1.0)
        assertThrows<IllegalStateException> { testInstance.errorGet(1) }
            .also { exception ->
                assertThat(exception.message, equalTo("Expected type to be error but was number"))
            }
    }

    @Test
    fun `Reads value on stack without popping the value`() {
        testInstance.errorPush(KuaError("Some Error Message"))
        assertThat(testInstance.errorGet(1), equalTo(KuaError("Some Error Message")))
        assertThat(testInstance.topGet(), equalTo(1))

        testInstance.errorPush(KuaError("Another Error Message"))
        assertThat(testInstance.errorGet(2), equalTo(KuaError("Another Error Message")))
        assertThat(testInstance.topGet(), equalTo(2))
    }

    @Test
    fun `Reads value on stack with negative index without popping the value`() {
        TODO()
//        testInstance.errorPush(KuaError("Some Error Message"))
//        testInstance.errorPush(KuaError("Some Error Message"))
//
//        val t = KuaTable(1, testInstance.sandbox)

//        println(t.getString("message"))
//        println(t.get(2))
//        println(t.length)

//        println(testInstance.topGet())

//        println(testInstance.errorGetString(-1))
//        assertThat(testInstance.errorGet(-1), equalTo(KuaError("Some Error Message")))
//        assertThat(testInstance.topGet(), equalTo(1))
//
//        testInstance.errorPush(KuaError("Another Error Message"))
//        assertThat(testInstance.errorGetString(-1), equalTo(KuaError("Some Error Message")))
//        assertThat(testInstance.topGet(), equalTo(2))
    }

}
package io.hamal.lib.kua.native

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class TableGetRawTest : NativeBaseTest() {

    @Test
    fun `Gets value from table`() {
        testInstance.tableCreate(0, 1)
        testInstance.pushString("key")
        testInstance.pushNumber(23.0)
        testInstance.tableSetRaw(1)
        assertThat(testInstance.top(), equalTo(1))

        testInstance.pushString("key")
        val result = testInstance.tableGetRaw(1)
        assertThat(result, equalTo(3))

        assertThat(testInstance.toNumber(-1), equalTo(23.0))
        assertThat(testInstance.top(), equalTo(2))
    }

    @Test
    fun `Tries to get value from table which key does not exists for`() {
        testInstance.tableCreate(0, 1)
        testInstance.pushString("key")
        testInstance.pushString("value")
        testInstance.tableSetRaw(1)

        testInstance.pushString("does-not-find-anything")
        val result = testInstance.tableGetRaw(1)
        assertThat(result, equalTo(0))

        assertThat(testInstance.type(-1), equalTo(0)) // Nil
        assertThat(testInstance.top(), equalTo(2))
    }

    @Test
    fun `Tries to get a value but not table`() {
        testInstance.pushNumber(2.34)
        assertThrows<IllegalStateException> {
            testInstance.pushString("key")
            testInstance.tableGetRaw(1)
        }.also { exception -> assertThat(exception.message, equalTo("Expected type to be table but was number")) }
    }

    @Test
    fun `Tries to get field from table but stack would overflow`() {
        testInstance.tableCreate(0, 1)
        testInstance.pushString("key")
        testInstance.pushString("value")
        testInstance.tableSetRaw(1)

        repeat(999997) { testInstance.pushBoolean(true) }

        assertThrows<IllegalArgumentException> {
            testInstance.pushString("key")
            testInstance.tableGetRaw(1)
        }.also { exception -> assertThat(exception.message, equalTo("Prevented stack overflow")) }
    }
}
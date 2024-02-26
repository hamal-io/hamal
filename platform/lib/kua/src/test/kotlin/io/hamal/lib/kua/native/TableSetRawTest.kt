package io.hamal.lib.kua.native

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class TableSetRawTest : NativeBaseTest() {

    @Test
    fun `Sets value to empty table`() {
        testInstance.tableCreate(0, 1)
        testInstance.pushString("key")
        testInstance.pushString("value")
        testInstance.tableSetRaw(1)
        assertThat(testInstance.top(), equalTo(1))

        testInstance.pushString("key")
        testInstance.tableGetRaw(1)
        assertThat(testInstance.toString(-1), equalTo("value"))
        assertThat(testInstance.tableGetLength(1), equalTo(1))
    }

    @Test
    fun `Sets different value for same key`() {
        testInstance.tableCreate(0, 1)
        testInstance.pushString("key")
        testInstance.pushString("value")
        testInstance.tableSetRaw(1)
        assertThat(testInstance.top(), equalTo(1))

        testInstance.pushString("key")
        testInstance.pushNumber(42.0)
        testInstance.tableSetRaw(1)

        testInstance.tableGetField(1, "key")
        assertThat(testInstance.toNumber(-1), equalTo(42.0))
        assertThat(testInstance.tableGetLength(1), equalTo(1))
    }

    @Test
    fun `Sets different value with different key`() {
        testInstance.tableCreate(0, 1)

        testInstance.pushString("key")
        testInstance.pushString("value")
        testInstance.tableSetRaw(1)

        testInstance.pushString("different")
        testInstance.pushNumber(42.0)
        testInstance.tableSetRaw(1)

        testInstance.tableGetField(1, "key")
        assertThat(testInstance.toString(-1), equalTo("value"))

        testInstance.tableGetField(1, "different")
        assertThat(testInstance.toNumber(-1), equalTo(42.0))

        assertThat(testInstance.tableGetLength(1), equalTo(2))
    }

    @Test
    fun `Tries to set raw value but not a table`() {
        testInstance.pushNumber(2.34)
        assertThrows<IllegalStateException> {
            testInstance.pushString("key")
            testInstance.pushString("value")
            testInstance.tableSetRaw(1)
        }.also { exception -> assertThat(exception.message, equalTo("Expected type to be table but was number")) }
    }
}
package io.hamal.lib.kua.native

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class TableNextTest : NativeBaseTest() {

    @Test
    fun `Next on empty table`() {
        testInstance.tableCreate(0, 0)
        testInstance.pushNil()
        val result = testInstance.tableNext(-2)
        assertThat(result, equalTo(false))

        testInstance.pop(1)
        verifyStackIsEmpty()
    }

    @Test
    fun `Next on table with single element`() {
        testInstance.tableCreate(0, 1)
        testInstance.pushString("key")
        testInstance.pushString("value")
        testInstance.tableSetRaw(1)

        testInstance.pushNil()
        val result = testInstance.tableNext(1)
        assertThat(result, equalTo(true))
        assertThat(testInstance.toString(-2), equalTo("key"))
        assertThat(testInstance.toString(-1), equalTo("value"))

        testInstance.pop(3)
        verifyStackIsEmpty()
    }

    @Test
    fun `Multiple next`() {
        testInstance.tableCreate(0, 1)

        repeat(1000) { idx ->
            testInstance.pushString("key-${idx}")
            testInstance.pushString("value-${idx}")
            testInstance.tableSetRaw(1)
        }

        val keys = mutableSetOf<String>()
        val values = mutableSetOf<String>()
        testInstance.pushNil()
        repeat(1000) {
            val result = testInstance.tableNext(1)
            assertThat(result, equalTo(true))
            keys.add(testInstance.toString(-2))
            values.add(testInstance.toString(-1))
            testInstance.pop(1)
        }

        assertThat(keys, hasSize(1000))
        assertThat(values, hasSize(1000))

        assertThat(testInstance.tableNext(1), equalTo(false))

        testInstance.pop(1)
        verifyStackIsEmpty()
    }

    @Test
    fun `Tries to run next but not a table`() {
        testInstance.pushNumber(2.34)
        assertThrows<IllegalStateException> { testInstance.tableNext(1) }
            .also { exception -> assertThat(exception.message, equalTo("Expected type to be table but was number")) }
    }
}
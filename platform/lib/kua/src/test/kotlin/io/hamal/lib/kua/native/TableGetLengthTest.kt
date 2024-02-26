package io.hamal.lib.kua.native

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class TableGetLengthTest : NativeBaseTest() {

    @Test
    fun `Size of empty table`() {
        testInstance.tableCreate(12, 12)
        val result = testInstance.tableGetLength(1)
        assertThat(result, equalTo(0))
    }

    @Test
    fun `Size of table with single field`() {
        testInstance.tableCreate(0, 1)
        testInstance.pushString("value")
        testInstance.tabletSetField(1, "key")
        assertThat(testInstance.top(), equalTo(1))

        val result = testInstance.tableGetLength(1)
        assertThat(result, equalTo(1))
        assertThat(testInstance.top(), equalTo(1))
    }

    @Test
    fun `Size of table with multiple fields`() {
        testInstance.tableCreate(0, 1)
        repeat(10) { idx ->
            testInstance.pushString("value")
            testInstance.tabletSetField(1, "key-${idx}")

            val result = testInstance.tableGetLength(1)
            assertThat(result, equalTo(idx + 1))
        }
        assertThat(testInstance.top(), equalTo(1))
    }

    @Test
    fun `Does not alter the stack`() {
        testInstance.tableCreate(0, 1)
        repeat(10) { idx ->
            testInstance.pushString("value")
            testInstance.tabletSetField(1, "key-${idx}")
            testInstance.tableGetField(1, "Key-${idx}")
        }

        assertThat(testInstance.top(), equalTo(11))

        val result = testInstance.tableGetLength(1)
        assertThat(result, equalTo(10))

        assertThat(testInstance.top(), equalTo(11))
    }

    @Test
    fun `Tries to get table size but not a table`() {
        testInstance.pushNumber(2.34)
        assertThrows<IllegalStateException> { testInstance.tableGetLength(1) }
            .also { exception -> assertThat(exception.message, equalTo("Expected type to be table but was number")) }
    }
}
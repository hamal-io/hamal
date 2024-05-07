package io.hamal.lib.kua.native

import io.hamal.lib.kua.ErrorIllegalState
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class TableLengthTest : NativeBaseTest() {

    @Test
    fun `Size of empty table`() {
        testInstance.tableCreate(12, 12)
        val result = testInstance.tableLength(1)
        assertThat(result, equalTo(0))
    }

    @Test
    fun `Size of table with single field`() {
        testInstance.tableCreate(0, 1)
        testInstance.stringPush("value")
        testInstance.tableFieldSet(1, "key")
        assertThat(testInstance.topGet(), equalTo(1))

        val result = testInstance.tableLength(1)
        assertThat(result, equalTo(1))
        assertThat(testInstance.topGet(), equalTo(1))
    }

    @Test
    fun `Size of table with multiple fields`() {
        testInstance.tableCreate(0, 1)
        repeat(10) { idx ->
            testInstance.stringPush("value")
            testInstance.tableFieldSet(1, "key-${idx}")

            val result = testInstance.tableLength(1)
            assertThat(result, equalTo(idx + 1))
        }
        assertThat(testInstance.topGet(), equalTo(1))
    }

    @Test
    fun `Does not alter the stack`() {
        testInstance.tableCreate(0, 1)
        repeat(10) { idx ->
            testInstance.stringPush("value")
            testInstance.tableFieldSet(1, "key-${idx}")
            testInstance.tableFieldGet(1, "Key-${idx}")
        }

        assertThat(testInstance.topGet(), equalTo(11))

        val result = testInstance.tableLength(1)
        assertThat(result, equalTo(10))

        assertThat(testInstance.topGet(), equalTo(11))
    }

    @Test
    fun `Tries to get table size but not a table`() {
        testInstance.numberPush(2.34)
        assertThrows<ErrorIllegalState> { testInstance.tableLength(1) }
            .also { exception -> assertThat(exception.message, equalTo("Expected type to be table but was number")) }
    }
}
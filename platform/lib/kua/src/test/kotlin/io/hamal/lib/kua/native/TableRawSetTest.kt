package io.hamal.lib.kua.native

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class TableRawSetTest : NativeBaseTest() {

    @Test
    fun `Sets value to empty table`() {
        testInstance.tableCreate(0, 1)
        testInstance.stringPush("key")
        testInstance.stringPush("value")
        testInstance.tableRawSet(1)
        assertThat(testInstance.topGet(), equalTo(1))

        testInstance.stringPush("key")
        testInstance.tableRawGet(1)
        assertThat(testInstance.stringGet(-1), equalTo("value"))
        assertThat(testInstance.tableLength(1), equalTo(1))
    }

    @Test
    fun `Sets different value for same key`() {
        testInstance.tableCreate(0, 1)
        testInstance.stringPush("key")
        testInstance.stringPush("value")
        testInstance.tableRawSet(1)
        assertThat(testInstance.topGet(), equalTo(1))

        testInstance.stringPush("key")
        testInstance.numberPush(42.0)
        testInstance.tableRawSet(1)

        testInstance.tableFieldGet(1, "key")
        assertThat(testInstance.numberGet(-1), equalTo(42.0))
        assertThat(testInstance.tableLength(1), equalTo(1))
    }

    @Test
    fun `Sets different value with different key`() {
        testInstance.tableCreate(0, 1)

        testInstance.stringPush("key")
        testInstance.stringPush("value")
        testInstance.tableRawSet(1)

        testInstance.stringPush("different")
        testInstance.numberPush(42.0)
        testInstance.tableRawSet(1)

        testInstance.tableFieldGet(1, "key")
        assertThat(testInstance.stringGet(-1), equalTo("value"))

        testInstance.tableFieldGet(1, "different")
        assertThat(testInstance.numberGet(-1), equalTo(42.0))

        assertThat(testInstance.tableLength(1), equalTo(2))
    }

    @Test
    fun `Tries to set raw value but not a table`() {
        testInstance.numberPush(2.34)
        assertThrows<IllegalStateException> {
            testInstance.stringPush("key")
            testInstance.stringPush("value")
            testInstance.tableRawSet(1)
        }.also { exception -> assertThat(exception.message, equalTo("Expected type to be table but was number")) }
    }
}
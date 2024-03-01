package io.hamal.lib.kua.native

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class TableRawSetIdxTest : NativeBaseTest() {

    @Test
    fun `Sets value to empty table`() {
        testInstance.tableCreate(1, 0)
        testInstance.stringPush("value")
        testInstance.tableRawSetIdx(1, 23)
        assertThat(testInstance.topGet(), equalTo(1))

        testInstance.numberPush(23.0)
        testInstance.tableRawGet(1)
        assertThat(testInstance.stringGet(-1), equalTo("value"))
        assertThat(testInstance.tableLength(1), equalTo(1))
    }

    @Test
    fun `Sets different value for same index`() {
        testInstance.tableCreate(0, 1)
        testInstance.stringPush("value")
        testInstance.tableRawSetIdx(1, 23)
        assertThat(testInstance.topGet(), equalTo(1))

        testInstance.numberPush(42.0)
        testInstance.tableRawSetIdx(1, 23)

        testInstance.numberPush(23.0)
        testInstance.tableRawGet(1)
        assertThat(testInstance.numberGet(-1), equalTo(42.0))
        assertThat(testInstance.tableLength(1), equalTo(1))
    }

    @Test
    fun `Sets different value with different key`() {
        testInstance.tableCreate(0, 1)

        testInstance.stringPush("value")
        testInstance.tableRawSetIdx(1, 2)

        testInstance.numberPush(42.0)
        testInstance.tableRawSetIdx(1, 4)

        testInstance.tableRawGetIdx(1, 2)
        assertThat(testInstance.stringGet(-1), equalTo("value"))

        testInstance.tableRawGetIdx(1, 4)
        assertThat(testInstance.numberGet(-1), equalTo(42.0))

        assertThat(testInstance.tableLength(1), equalTo(2))
    }

    @Test
    fun `Tries to set a raw value but not a table`() {
        testInstance.numberPush(2.34)
        assertThrows<IllegalStateException> {
            testInstance.stringPush("value")
            testInstance.tableRawSetIdx(1, 4)
        }.also { exception -> assertThat(exception.message, equalTo("Expected type to be table but was number")) }
    }
}

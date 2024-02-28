package io.hamal.lib.kua.native

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class TableFieldSetTest : NativeBaseTest() {

    @Test
    fun `Sets value to empty table`() {
        testInstance.tableCreate(0, 1)
        testInstance.stringPush("value")
        testInstance.tableFieldSet(1, "key")
        assertThat(testInstance.topGet(), equalTo(1))

        testInstance.tableFieldGet(1, "key")
        assertThat(testInstance.stringGet(-1), equalTo("value"))
        assertThat(testInstance.tableLength(1), equalTo(1))
    }

    @Test
    fun `Sets different value for same key table`() {
        testInstance.tableCreate(0, 1)
        testInstance.stringPush("value")
        testInstance.tableFieldSet(1, "key")

        testInstance.numberPush(42.0)
        testInstance.tableFieldSet(1, "key")

        testInstance.tableFieldGet(1, "key")
        assertThat(testInstance.numberGet(-1), equalTo(42.0))
        assertThat(testInstance.tableLength(1), equalTo(1))
    }

    @Test
    fun `Sets different value with different key table`() {
        testInstance.tableCreate(0, 1)
        testInstance.stringPush("value")
        testInstance.tableFieldSet(1, "key")

        testInstance.numberPush(42.0)
        testInstance.tableFieldSet(1, "different")

        testInstance.tableFieldGet(1, "key")
        assertThat(testInstance.stringGet(-1), equalTo("value"))

        testInstance.tableFieldGet(1, "different")
        assertThat(testInstance.numberGet(-1), equalTo(42.0))

        assertThat(testInstance.tableLength(1), equalTo(2))
    }

    @Test
    fun `Tries to set a value but not a table`() {
        testInstance.numberPush(2.34)
        assertThrows<IllegalStateException> { testInstance.tableFieldSet(1, "key") }
            .also { exception -> assertThat(exception.message, equalTo("Expected type to be table but was number")) }
    }
}

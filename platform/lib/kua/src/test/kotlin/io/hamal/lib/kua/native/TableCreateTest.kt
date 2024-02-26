package io.hamal.lib.kua.native

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class TableCreateTest : NativeBaseTest() {

    @Test
    fun `Creates an empty table on empty stack`() {
        val result = testInstance.tableCreate(1, 2)
        assertThat(result, equalTo(1))
        assertThat(testInstance.topGet(), equalTo(1))

        assertThat(testInstance.type(1), equalTo(5))
    }

    @Test
    fun `Array count must not be negative`() {
        assertThrows<IllegalArgumentException> { testInstance.tableCreate(-1, 0) }
            .also { exception -> assertThat(exception.message, equalTo("Array count must not be negative")) }
    }

    @Test
    fun `Records count must not be negative`() {
        assertThrows<IllegalArgumentException> { testInstance.tableCreate(0, -1) }
            .also { exception -> assertThat(exception.message, equalTo("Records count must not be negative")) }
    }
}
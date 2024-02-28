package io.hamal.lib.kua.native

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class TableGetTest : NativeBaseTest() {

    @Test
    fun `Reads value on stack without popping the value`() {
        testInstance.tableCreate(0, 0)
        assertThat(testInstance.tableGet(1), equalTo(1))
        assertThat(testInstance.topGet(), equalTo(1))

        testInstance.tableCreate(0, 0)
        assertThat(testInstance.tableGet(2), equalTo(2))
        assertThat(testInstance.topGet(), equalTo(2))
    }

    @Test
    fun `Reads value on stack with negative index without popping the value`() {
        testInstance.tableCreate(0, 0)
        assertThat(testInstance.tableGet(1), equalTo(1))
        assertThat(testInstance.topGet(), equalTo(1))

        testInstance.tableCreate(0, 0)
        assertThat(testInstance.tableGet(-1), equalTo(2))
        assertThat(testInstance.topGet(), equalTo(2))
    }


    @Test
    fun `Tries to read table with 0 index`() {
        testInstance.tableCreate(0, 0)
        assertThrows<IllegalArgumentException> { testInstance.tableGet(0) }
            .also { exception -> assertThat(exception.message, equalTo("Index must not be 0")) }
    }

    @Test
    fun `Tries to read table with index bigger than stack size`() {
        testInstance.tableCreate(0, 0)
        assertThrows<IllegalArgumentException> { testInstance.tableGet(2) }
            .also { exception -> assertThat(exception.message, equalTo("Index out of bounds")) }
    }

    @Test
    fun `Tries to read table with abs(negative index) bigger than stack size`() {
        testInstance.tableCreate(0, 0)
        assertThrows<IllegalArgumentException> { testInstance.tableGet(2) }
            .also { exception -> assertThat(exception.message, equalTo("Index out of bounds")) }
    }

    @Test
    fun `Tries to read number as a table`() {
        testInstance.numberPush(1.0)
        assertThrows<IllegalStateException> { testInstance.tableGet(1) }
            .also { exception ->
                assertThat(
                    exception.message, equalTo("Expected type to be table but was number")
                )
            }
    }
}
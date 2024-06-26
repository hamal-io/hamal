package io.hamal.lib.kua.native

import io.hamal.lib.kua.ErrorIllegalArgument
import io.hamal.lib.kua.ErrorIllegalState
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class TableRawGetTest : NativeBaseTest() {

    @Test
    fun `Gets value from table`() {
        testInstance.tableCreate(0, 1)
        testInstance.stringPush("key")
        testInstance.numberPush(23.0)
        testInstance.tableRawSet(1)
        assertThat(testInstance.topGet(), equalTo(1))

        testInstance.stringPush("key")
        val result = testInstance.tableRawGet(1)
        assertThat(result, equalTo(3))

        assertThat(testInstance.numberGet(-1), equalTo(23.0))
        assertThat(testInstance.topGet(), equalTo(2))
    }

    @Test
    fun `Tries to get value from table , but key does not exists`() {
        testInstance.tableCreate(0, 1)
        testInstance.stringPush("key")
        testInstance.stringPush("value")
        testInstance.tableRawSet(1)

        testInstance.stringPush("does-not-find-anything")
        val result = testInstance.tableRawGet(1)
        assertThat(result, equalTo(0))

        assertThat(testInstance.type(-1), equalTo(0)) // Nil
        assertThat(testInstance.topGet(), equalTo(2))
    }

    @Test
    fun `Tries to get a value but not table`() {
        testInstance.numberPush(2.34)
        assertThrows<ErrorIllegalState> {
            testInstance.stringPush("key")
            testInstance.tableRawGet(1)
        }.also { exception -> assertThat(exception.message, equalTo("Expected type to be table but was number")) }
    }

    @Test
    fun `Tries to get field from table but stack would overflow`() {
        testInstance.tableCreate(0, 1)
        testInstance.stringPush("key")
        testInstance.stringPush("value")
        testInstance.tableRawSet(1)

        repeat(999997) { testInstance.booleanPush(true) }

        assertThrows<ErrorIllegalArgument> {
            testInstance.stringPush("key")
            testInstance.tableRawGet(1)
        }.also { exception -> assertThat(exception.message, equalTo("Prevented stack overflow")) }
    }
}
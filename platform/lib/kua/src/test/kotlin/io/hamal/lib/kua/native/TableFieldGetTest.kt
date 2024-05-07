package io.hamal.lib.kua.native

import io.hamal.lib.kua.ErrorIllegalArgument
import io.hamal.lib.kua.ErrorIllegalState
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class TableFieldGetTest : NativeBaseTest() {

    @Test
    fun `Gets value from table`() {
        testInstance.tableCreate(0, 1)
        testInstance.stringPush("value")
        testInstance.tableFieldSet(1, "key")
        assertThat(testInstance.topGet(), equalTo(1))

        val result = testInstance.tableFieldGet(1, "key")
        assertThat(result, equalTo(4))
        assertThat(testInstance.stringGet(-1), equalTo("value"))
        assertThat(testInstance.topGet(), equalTo(2))
    }

    @Test
    fun `Tries to get value from table , but key does not exists`() {
        testInstance.tableCreate(0, 1)
        testInstance.stringPush("value")
        testInstance.tableFieldSet(1, "key")

        val result = testInstance.tableFieldGet(1, "does-not-find-anything")
        assertThat(result, equalTo(0))
        assertThat(testInstance.type(-1), equalTo(0)) // Nil
        assertThat(testInstance.topGet(), equalTo(2))
    }

    @Test
    fun `Tries to get a value but not a table`() {
        testInstance.numberPush(2.34)
        assertThrows<ErrorIllegalState> { testInstance.tableFieldGet(1, "key") }
            .also { exception -> assertThat(exception.message, equalTo("Expected type to be table but was number")) }
    }

    @Test
    fun `Tries to get field from table but stack would overflow`() {
        testInstance.tableCreate(0, 1)
        testInstance.stringPush("value")
        testInstance.tableFieldSet(1, "key")

        repeat(999998) { testInstance.booleanPush(true) }

        assertThrows<ErrorIllegalArgument> { testInstance.tableFieldGet(1, "key") }
            .also { exception -> assertThat(exception.message, equalTo("Prevented stack overflow")) }
    }
}
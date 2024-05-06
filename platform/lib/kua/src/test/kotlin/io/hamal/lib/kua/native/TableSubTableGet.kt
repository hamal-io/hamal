package io.hamal.lib.kua.native

import io.hamal.lib.kua.ErrorIllegalArgument
import io.hamal.lib.kua.ErrorIllegalState
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class TableSubTableGetTest : NativeBaseTest() {

    @Test
    fun `Get table from table`() {
        testInstance.tableCreate(0, 0)

        testInstance.tableCreate(0, 1)
        testInstance.stringPush("rocks")
        testInstance.tableFieldSet(2, "hamal")
        testInstance.tableFieldSet(1, "nested")

        assertThat(testInstance.topGet(), equalTo(1))

        testInstance.tableSubTableGet(1, "nested")
        assertThat(testInstance.topGet(), equalTo(2))
        assertThat(testInstance.type(2), equalTo(5))

        testInstance.tableFieldGet(2, "hamal")
        assertThat(testInstance.stringGet(3), equalTo("rocks"))
        assertThat(testInstance.topGet(), equalTo(3))
    }

    @Test
    fun `Tries to find sub table, but fails due to no table exist with current key`() {
        testInstance.tableCreate(0, 0)

        testInstance.tableCreate(0, 1)
        testInstance.stringPush("rocks")
        testInstance.tableFieldSet(2, "hamal")
        testInstance.tableFieldSet(1, "nested")

        assertThat(testInstance.topGet(), equalTo(1))

        assertThrows<ErrorIllegalState> { testInstance.tableSubTableGet(1, "does not exists") }
            .also { exception -> assertThat(exception.message, equalTo("Expected type to be table but was nil")) }

        assertThat(testInstance.topGet(), equalTo(1))
    }


    @Test
    fun `Does not overwrite value when value of key is not a table`() {
        testInstance.tableCreate(0, 0)
        testInstance.stringPush("rocks")
        testInstance.tableFieldSet(1, "hamal")

        assertThrows<ErrorIllegalState> {
            testInstance.tableSubTableGet(1, "hamal")
        }.also { exception -> assertThat(exception.message, equalTo("Expected type to be table but was string")) }

        testInstance.tableFieldGet(1, "hamal")
        assertThat(testInstance.topGet(), equalTo(2))
        assertThat(testInstance.type(2), equalTo(4))
        assertThat(testInstance.stringGet(2), equalTo("rocks"))
    }

    @Test
    fun `Tries to get sub table from table but stack would overflow`() {
        testInstance.tableCreate(0, 0)

        testInstance.tableCreate(0, 1)
        testInstance.stringPush("rocks")
        testInstance.tableFieldSet(2, "hamal")
        testInstance.tableFieldSet(1, "nested")

        repeat(999998) { testInstance.booleanPush(true) }

        assertThrows<ErrorIllegalArgument> { testInstance.tableFieldGet(1, "nested") }
            .also { exception ->
                assertThat(
                    exception.message, equalTo("Prevented stack overflow")
                )
            }
    }
}
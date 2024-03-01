package io.hamal.lib.kua.native

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class TableGetSubTableTest : NativeBaseTest() {

    @Test
    fun `Get sub table from table`() {
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
    fun `Creates a new table if no table exist with current key`() {
        testInstance.tableCreate(0, 0)

        testInstance.tableCreate(0, 1)
        testInstance.stringPush("rocks")
        testInstance.tableFieldSet(2, "hamal")
        testInstance.tableFieldSet(1, "nested")

        assertThat(testInstance.topGet(), equalTo(1))

        testInstance.tableSubTableGet(1, "does not exists")
        assertThat(testInstance.topGet(), equalTo(2))
        assertThat(testInstance.type(2), equalTo(5))
        assertThat(testInstance.tableLength(2), equalTo(0))
    }


    @Test
    fun `Overwrites value when value of key is not a table`() {
        testInstance.tableCreate(0, 0)
        testInstance.stringPush("rocks")
        testInstance.tableFieldSet(1, "hamal")

        testInstance.tableSubTableGet(1, "hamal")

        testInstance.tableFieldGet(1, "hamal")
        assertThat(testInstance.topGet(), equalTo(3))
        assertThat(testInstance.type(3), equalTo(5))
    }

    @Test
    fun `Tries to get sub table from table but stack would overflow`() {
        testInstance.tableCreate(0, 0)

        testInstance.tableCreate(0, 1)
        testInstance.stringPush("rocks")
        testInstance.tableFieldSet(2, "hamal")
        testInstance.tableFieldSet(1, "nested")

        repeat(999998) { testInstance.booleanPush(true) }

        assertThrows<IllegalArgumentException> { testInstance.tableFieldGet(1, "nested") }
            .also { exception ->
                assertThat(
                    exception.message, equalTo("Prevented stack overflow")
                )
            }
    }
}
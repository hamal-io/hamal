package io.hamal.lib.kua.native

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class TableGetFieldTest : NativeBaseTest() {

    @Test
    fun `Gets value from table`() {
        testInstance.tableCreate(0, 1)
        testInstance.pushString("value")
        testInstance.tabletSetField(1, "key")
        assertThat(testInstance.top(), equalTo(1))

        val result = testInstance.tableGetField(1, "key")
        assertThat(result, equalTo(4))
        assertThat(testInstance.toString(-1), equalTo("value"))
        assertThat(testInstance.top(), equalTo(2))
    }

    @Test
    fun `Tries to get value from table which key does not exists for`() {
        testInstance.tableCreate(0, 1)
        testInstance.pushString("value")
        testInstance.tabletSetField(1, "key")

        val result = testInstance.tableGetField(1, "does-not-find-anything")
        assertThat(result, equalTo(0))
        assertThat(testInstance.type(-1), equalTo(0)) // Nil
        assertThat(testInstance.top(), equalTo(2))
    }

    @Test
    fun `Tries to get a value but not a table`() {
        testInstance.pushNumber(2.34)
        assertThrows<IllegalStateException> { testInstance.tableGetField(1, "key") }
            .also { exception -> assertThat(exception.message, equalTo("Expected type to be table but was number")) }
    }

    @Test
    fun `Tries to get field from table but stack would overflow`() {
        testInstance.tableCreate(0, 1)
        testInstance.pushString("value")
        testInstance.tabletSetField(1, "key")

        repeat(999998) { testInstance.pushBoolean(true) }

        assertThrows<IllegalArgumentException> { testInstance.tableGetField(1, "key") }
            .also { exception -> assertThat(exception.message, equalTo("Prevented stack overflow")) }
    }
}
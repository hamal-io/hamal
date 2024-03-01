package io.hamal.lib.kua.native

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class TableGetRawIdxTest : NativeBaseTest() {

    @Test
    fun `Gets value from table`() {
        testInstance.tableCreate(0, 1)
        testInstance.numberPush(5.0)
        testInstance.stringPush("value")
        testInstance.tableSetRaw(1)
        assertThat(testInstance.topGet(), equalTo(1))

        val result = testInstance.tableGetRawIdx(1, 5)
        assertThat(result, equalTo(4))
        assertThat(testInstance.stringGet(-1), equalTo("value"))
        assertThat(testInstance.topGet(), equalTo(2))
    }

    @Test
    fun `Tries to get value from table which key does not exists for`() {
        testInstance.tableCreate(0, 1)
        testInstance.numberPush(43.0)
        testInstance.stringPush("value")
        testInstance.tableSetRaw(1)

        val result = testInstance.tableGetRawIdx(1, 1337)
        assertThat(result, equalTo(0))
        assertThat(testInstance.type(-1), equalTo(0)) // Nil
        assertThat(testInstance.topGet(), equalTo(2))
    }

    @Test
    fun `Tries to get a value but not table`() {
        testInstance.numberPush(2.34)
        assertThrows<IllegalStateException> { testInstance.tableGetRawIdx(1, 3) }
            .also { exception -> assertThat(exception.message, equalTo("Expected type to be table but was number")) }
    }

    @Test
    fun `Tries to get field from table but stack would overflow`() {
        testInstance.tableCreate(0, 1)
        testInstance.stringPush("value")
        testInstance.tableSetRawIdx(1, 1)

        repeat(999998) { testInstance.booleanPush(true) }

        assertThrows<IllegalArgumentException> { testInstance.tableGetRawIdx(1, 1) }
            .also { exception -> assertThat(exception.message, equalTo("Prevented stack overflow")) }
    }
}
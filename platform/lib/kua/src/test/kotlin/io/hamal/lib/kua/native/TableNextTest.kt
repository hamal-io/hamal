package io.hamal.lib.kua.native

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class TableNextTest : NativeBaseTest() {

    @Test
    fun `Next on empty table`() {
        testInstance.tableCreate(0, 0)
        testInstance.nilPush()
        val result = testInstance.tableNext(-2)
        assertThat(result, equalTo(false))

        testInstance.topPop(1)
        verifyStackIsEmpty()
    }

    @Test
    fun `Next on table with single element`() {
        testInstance.tableCreate(0, 1)
        testInstance.stringPush("key")
        testInstance.stringPush("value")
        testInstance.tableRawSet(1)

        testInstance.nilPush()
        val result = testInstance.tableNext(1)
        assertThat(result, equalTo(true))
        assertThat(testInstance.stringGet(-2), equalTo("key"))
        assertThat(testInstance.stringGet(-1), equalTo("value"))

        testInstance.topPop(3)
        verifyStackIsEmpty()
    }

    @Test
    fun `Multiple next`() {
        testInstance.tableCreate(0, 1)

        repeat(1000) { idx ->
            testInstance.stringPush("key-${idx}")
            testInstance.stringPush("value-${idx}")
            testInstance.tableRawSet(1)
        }

        val keys = mutableSetOf<String>()
        val values = mutableSetOf<String>()
        testInstance.nilPush()
        repeat(1000) {
            val result = testInstance.tableNext(1)
            assertThat(result, equalTo(true))
            keys.add(testInstance.stringGet(-2))
            values.add(testInstance.stringGet(-1))
            testInstance.topPop(1)
        }

        assertThat(keys, hasSize(1000))
        assertThat(values, hasSize(1000))

        assertThat(testInstance.tableNext(1), equalTo(false))

        testInstance.topPop(1)
        verifyStackIsEmpty()
    }

    @Test
    fun `Tries to run next but not a table`() {
        testInstance.numberPush(2.34)
        assertThrows<IllegalStateException> { testInstance.tableNext(1) }
            .also { exception -> assertThat(exception.message, equalTo("Expected type to be table but was number")) }
    }
}
package io.hamal.lib.kua.native

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class TableAppendTest : NativeBaseTest() {

    @Test
    fun `Append value to empty table`() {
        testInstance.tableCreate(0, 0)
        testInstance.numberPush(512.0)
        testInstance.tableAppend(1)

        testInstance.topPop(1)
        verifyStackIsEmpty()
    }

    @Test
    fun `Append multiple values to table`() {
        testInstance.tableCreate(1000, 0)
        repeat(1000) { idx ->
            testInstance.numberPush(idx.toDouble())
            val result = testInstance.tableAppend(1)
            assertThat(result, equalTo(idx + 1))
        }
    }

    @Test
    fun `Tries to insert into table but not a table`() {
        testInstance.numberPush(2.34)
        assertThrows<IllegalStateException> { testInstance.tableAppend(1) }
            .also { exception -> assertThat(exception.message, equalTo("Expected type to be table but was number")) }
    }
}
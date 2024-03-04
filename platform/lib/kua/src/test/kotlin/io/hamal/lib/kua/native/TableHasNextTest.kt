package io.hamal.lib.kua.native

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class TableHasNextTest : NativeBaseTest() {

    @Test
    fun `On empty table`() {
        testInstance.tableCreate(0, 0)
        testInstance.nilPush()
        val result = testInstance.tableHasNext(1)
        assertThat(result, equalTo(false))

        testInstance.topPop(2)
        verifyStackIsEmpty()
    }

    @Test
    fun `On table with single element`() {
        testInstance.tableCreate(0, 1)
        testInstance.stringPush("key")
        testInstance.stringPush("value")
        testInstance.tableRawSet(1)

        testInstance.nilPush()
        val result = testInstance.tableHasNext(1)
        assertThat(result, equalTo(true))

        testInstance.topPop(2)
        verifyStackIsEmpty()
    }

    @Test
    fun `Can invoke tableHasNext multiple times`() {
        testInstance.tableCreate(0, 1)
        testInstance.stringPush("key")
        testInstance.stringPush("value")
        testInstance.tableRawSet(1)

        testInstance.nilPush()
        testInstance.tableHasNext(1).also { result -> assertThat(result, equalTo(true)) }
        testInstance.tableHasNext(1).also { result -> assertThat(result, equalTo(true)) }
        testInstance.tableHasNext(1).also { result -> assertThat(result, equalTo(true)) }
        testInstance.tableHasNext(1).also { result -> assertThat(result, equalTo(true)) }
        testInstance.tableHasNext(1).also { result -> assertThat(result, equalTo(true)) }
        testInstance.tableHasNext(1).also { result -> assertThat(result, equalTo(true)) }
        testInstance.tableHasNext(1).also { result -> assertThat(result, equalTo(true)) }
        testInstance.tableHasNext(1).also { result -> assertThat(result, equalTo(true)) }
        testInstance.tableHasNext(1).also { result -> assertThat(result, equalTo(true)) }

        testInstance.topPop(2)
        verifyStackIsEmpty()
    }


    @Test
    fun `On table with multiple elements`() {
        testInstance.tableCreate(0, 1)

        repeat(10) { idx ->
            testInstance.stringPush("key-${idx}")
            testInstance.stringPush("value-${idx}")
            testInstance.tableRawSet(1)
        }

        testInstance.nilPush()
        val result = testInstance.tableHasNext(1)
        assertThat(result, equalTo(true))


        testInstance.topPop(2)
        verifyStackIsEmpty()
    }

    @Test
    @Disabled
    fun `Tries to run has next - but key for iteration (nilPush) has never pushed`() {
        testInstance.tableCreate(0, 1)
        testInstance.stringPush("key")
        testInstance.stringPush("value")
        testInstance.tableRawSet(1)

        assertThrows<IllegalStateException> {
            testInstance.tableHasNext(1)
        }.also { exception -> assertThat(exception.message, equalTo("No key was pushed onto stack")) }
    }
}
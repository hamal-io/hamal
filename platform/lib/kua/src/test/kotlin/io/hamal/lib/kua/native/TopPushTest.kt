package io.hamal.lib.kua.native

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class TopPushTest : NativeBaseTest() {

    @Test
    fun `Pushes value at the top of stack which is already at the top`() {
        testInstance.booleanPush(true)
        testInstance.booleanPush(false)
        testInstance.numberPush(13.37)
        testInstance.topPush(3)

        assertThat(testInstance.type(3), equalTo(3))
        assertThat(testInstance.type(4), equalTo(3))
        assertThat(testInstance.numberGet(3), equalTo(13.37))
        assertThat(testInstance.numberGet(4), equalTo(13.37))
        assertThat(testInstance.topGet(), equalTo(4))
    }

    @Test
    fun `Pushes table at the top of the stack`() {
        testInstance.tableCreate(0, 0)
        testInstance.nilPush()
        testInstance.booleanPush(true)
        testInstance.numberPush(42.0)
        testInstance.stringPush("Hamal Rocks")
        assertThat(testInstance.type(-1), equalTo(4))

        testInstance.topPush(1)
        assertThat(testInstance.type(1), equalTo(5))
        assertThat(testInstance.type(-1), equalTo(5))
        assertThat(testInstance.topGet(), equalTo(6))
    }

    @Test
    fun `Pushing value outside of stack causes pushing nil`() {
        verifyStackIsEmpty()

        val result = testInstance.topPush(1337)
        assertThat(result, equalTo(1))

        assertThat(testInstance.type(-1), equalTo(0))

        val secondResult = testInstance.topPush(23)
        assertThat(secondResult, equalTo(2))

        assertThat(testInstance.type(-1), equalTo(0))
        assertThat(testInstance.type(-2), equalTo(0))

        assertThat(testInstance.topGet(), equalTo(2))
    }

    @Test
    fun `Tries to push too many items on the stack limited to 999_999`() {
        testInstance.booleanPush(true)
        repeat(999_998) { idx ->
            val result = testInstance.topPush(1)
            assertThat(result, equalTo(idx + 2))
        }

        assertThrows<IllegalArgumentException> {
            testInstance.topPush(1)
        }.also { exception ->
            assertThat(exception.message, equalTo("Prevented stack overflow"))
        }
    }
}
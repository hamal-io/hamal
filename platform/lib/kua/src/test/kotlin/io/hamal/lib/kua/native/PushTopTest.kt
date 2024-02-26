package io.hamal.lib.kua.native

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class PushTopTest : NativeBaseTest() {

    @Test
    fun `Pushes value at the top of stack which is already at the top`() {
        testInstance.pushBoolean(true)
        testInstance.pushBoolean(false)
        testInstance.pushNumber(13.37)
        testInstance.pushTop(3)

        assertThat(testInstance.type(3), equalTo(3))
        assertThat(testInstance.type(4), equalTo(3))
        assertThat(testInstance.toNumber(3), equalTo(13.37))
        assertThat(testInstance.toNumber(4), equalTo(13.37))
        assertThat(testInstance.top(), equalTo(4))
    }

    @Test
    fun `Pushes table at the top of the stack`() {
        testInstance.tableCreate(0, 0)
        testInstance.pushNil()
        testInstance.pushBoolean(true)
        testInstance.pushNumber(42.0)
        testInstance.pushString("Hamal Rocks")
        assertThat(testInstance.type(-1), equalTo(4))

        testInstance.pushTop(1)
        assertThat(testInstance.type(1), equalTo(5))
        assertThat(testInstance.type(-1), equalTo(5))
        assertThat(testInstance.top(), equalTo(6))
    }

    @Test
    fun `Pushing value outside of stack causes pushing nil`() {
        verifyStackIsEmpty()

        val result = testInstance.pushTop(1337)
        assertThat(result, equalTo(1))

        assertThat(testInstance.type(-1), equalTo(0))

        val secondResult = testInstance.pushTop(23)
        assertThat(secondResult, equalTo(2))

        assertThat(testInstance.type(-1), equalTo(0))
        assertThat(testInstance.type(-2), equalTo(0))

        assertThat(testInstance.top(), equalTo(2))
    }

    @Test
    fun `Tries to push too many items on the stack limited to 999_999`() {
        testInstance.pushBoolean(true)
        repeat(999_998) { idx ->
            val result = testInstance.pushTop(1)
            assertThat(result, equalTo(idx + 2))
        }

        assertThrows<IllegalArgumentException> {
            testInstance.pushTop(1)
        }.also { exception ->
            assertThat(exception.message, equalTo("Prevented stack overflow"))
        }
    }
}
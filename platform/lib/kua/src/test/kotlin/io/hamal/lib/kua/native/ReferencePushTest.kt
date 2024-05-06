package io.hamal.lib.kua.native

import io.hamal.lib.kua.ErrorIllegalArgument
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class ReferencePushTest : NativeBaseTest() {

    @Test
    fun `Pushes reference on stack`() {
        testInstance.stringPush("hamal rocks")
        val ref = testInstance.referenceAcquire()

        testInstance.referencePush(ref).also { top ->
            assertThat(top, equalTo(4))
        }
        assertThat(testInstance.topGet(), equalTo(1))
        assertThat(testInstance.stringGet(1), equalTo("hamal rocks"))
    }

    @Test
    fun `Pushes reference on stack multiple times`() {
        testInstance.stringPush("hamal rocks")
        val refOne = testInstance.referenceAcquire()

        testInstance.numberPush(13.37)
        val refTwo = testInstance.referenceAcquire()

        testInstance.referencePush(refOne).also { result ->
            assertThat(result, equalTo(4))
        }
        assertThat(testInstance.topGet(), equalTo(1))
        assertThat(testInstance.stringGet(1), equalTo("hamal rocks"))

        testInstance.referencePush(refTwo).also { top ->
            assertThat(top, equalTo(3))
        }
        assertThat(testInstance.topGet(), equalTo(2))
        assertThat(testInstance.numberGet(2), equalTo(13.37))

        testInstance.referencePush(refOne).also { top ->
            assertThat(top, equalTo(4))
        }
        assertThat(testInstance.topGet(), equalTo(3))
        assertThat(testInstance.stringGet(3), equalTo("hamal rocks"))

        testInstance.referencePush(refOne).also { top ->
            assertThat(top, equalTo(4))
        }
        assertThat(testInstance.topGet(), equalTo(4))
        assertThat(testInstance.stringGet(4), equalTo("hamal rocks"))
    }

    @Test
    fun `Tries to push reference which does not exist`() {
        testInstance.stringPush("hamal rocks")
        val ref = testInstance.referenceAcquire()

        testInstance.referencePush(1234567890)
        assertThat(testInstance.topGet(), equalTo(1))
        assertThat(testInstance.type(1), equalTo(0))
    }

    @Test
    fun `Tries to push too many items on the stack limited to 999_999`() {
        testInstance.stringPush("hamal rocks")
        val refOne = testInstance.referenceAcquire()

        repeat(999999) { testInstance.numberPush(it.toDouble()) }
        assertThrows<ErrorIllegalArgument> { testInstance.referencePush(refOne) }
            .also { exception -> assertThat(exception.message, equalTo("Prevented stack overflow")) }
    }
}
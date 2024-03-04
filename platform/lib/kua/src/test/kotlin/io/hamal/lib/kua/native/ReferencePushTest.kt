package io.hamal.lib.kua.native

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class ReferencePushTest : NativeBaseTest() {

    @Test
    fun `Pushes reference on stack`() {
        testInstance.stringPush("hamal rocks")
        val ref = testInstance.referenceAcquire()

        testInstance.referencePush(ref)
        assertThat(testInstance.topGet(), equalTo(1))
        assertThat(testInstance.stringGet(1), equalTo("hamal rocks"))
    }

    @Test
    fun `Pushes reference on stack multiple times`() {
        testInstance.stringPush("hamal rocks")
        val refOne = testInstance.referenceAcquire()

        testInstance.stringPush("hamal really rocks")
        val refTwo = testInstance.referenceAcquire()

        testInstance.referencePush(refOne)
        assertThat(testInstance.topGet(), equalTo(1))
        assertThat(testInstance.stringGet(1), equalTo("hamal rocks"))

        testInstance.referencePush(refTwo)
        assertThat(testInstance.topGet(), equalTo(2))
        assertThat(testInstance.stringGet(2), equalTo("hamal really rocks"))

        testInstance.referencePush(refOne)
        assertThat(testInstance.topGet(), equalTo(3))
        assertThat(testInstance.stringGet(3), equalTo("hamal rocks"))

        testInstance.referencePush(refOne)
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
        assertThrows<IllegalArgumentException> { testInstance.referencePush(refOne) }
            .also { exception -> assertThat(exception.message, equalTo("Prevented stack overflow")) }
    }
}
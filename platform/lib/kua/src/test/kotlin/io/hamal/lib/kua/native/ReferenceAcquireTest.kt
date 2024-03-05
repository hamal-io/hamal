package io.hamal.lib.kua.native

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class ReferenceAcquireTest : NativeBaseTest() {

    @Test
    fun `Acquires reference for table on top of the stack`() {
        testInstance.tableCreate(0, 0)
        testInstance.numberPush(42.0)
        testInstance.tableFieldSet(1, "answer")

        val reference: Int = testInstance.referenceAcquire()
        assertThat(testInstance.topGet(), equalTo(0))

        testInstance.referencePush(reference)

        assertThat(testInstance.topGet(), equalTo(1))
        assertThat(testInstance.type(1), equalTo(5))
        testInstance.tableFieldGet(1, "answer")
        assertThat(testInstance.numberGet(2), equalTo(42.0))
    }

    @Test
    fun `Tries to acquire reference on empty stack`() {
        assertThrows<IllegalArgumentException> { testInstance.referenceAcquire() }
            .also { exception -> assertThat(exception.message, equalTo("Prevented stack underflow")) }
    }

}
package io.hamal.lib.kua.native

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class GlobalSetTest : NativeBaseTest() {

    @Test
    fun `Sets number as global and removes from stack`() {
        testInstance.numberPush(42.0)
        assertThat(testInstance.topGet(), equalTo(1))

        testInstance.globalSet("answer")
        assertThat(testInstance.topGet(), equalTo(0))

        testInstance.globalGet("answer")
        assertThat(testInstance.numberGet(1), equalTo(42.0))
    }

    @Test
    fun `Overwrites global`() {
        testInstance.numberPush(42.0)
        testInstance.globalSet("answer")

        testInstance.numberPush(24.0)
        testInstance.globalGet("answer")

        testInstance.globalGet("answer")
        assertThat(testInstance.numberGet(1), equalTo(24.0))
    }

    @Test
    fun `Can have multiple global variables`() {
        testInstance.numberPush(42.0)
        testInstance.globalSet("key-one")

        testInstance.numberPush(24.0)
        testInstance.globalSet("key-two")

        testInstance.globalGet("key-one")
        assertThat(testInstance.numberGet(-1), equalTo(42.0))

        testInstance.globalGet("key-two")
        assertThat(testInstance.numberGet(-1), equalTo(24.0))
    }

}
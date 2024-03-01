package io.hamal.lib.kua.native

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class GlobalGetTest : NativeBaseTest() {

    @Test
    fun `Gets global onto stack`() {
        testInstance.numberPush(42.0)
        testInstance.globalSet("answer")
        assertThat(testInstance.topGet(), equalTo(0))

        testInstance.globalGet("answer")
        assertThat(testInstance.numberGet(1), equalTo(42.0))
        assertThat(testInstance.topGet(), equalTo(1))
    }


    @Test
    fun `Tries to load global, but does not exist`() {
        testInstance.numberPush(42.0)
        testInstance.globalSet("hamal")
        assertThat(testInstance.topGet(), equalTo(0))

        testInstance.globalGet("answer")
        assertThat(testInstance.type(1), equalTo(0))
        assertThat(testInstance.topGet(), equalTo(1))
    }

    @Test
    fun `Tries to get global but prevents stack overflow`() {
        testInstance.numberPush(42.0)
        testInstance.globalSet("hamal")
        repeat(999999) { testInstance.booleanPush(true) }
        assertThrows<IllegalArgumentException> { testInstance.globalGet("hamal") }
            .also { exception -> assertThat(exception.message, equalTo("Prevented stack overflow")) }
    }

}
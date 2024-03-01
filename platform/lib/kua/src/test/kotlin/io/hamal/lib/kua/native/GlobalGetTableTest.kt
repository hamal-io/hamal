package io.hamal.lib.kua.native

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class GlobalGetTableTest : NativeBaseTest() {

    @Test
    fun `Gets global table onto stack`() {
        testInstance.tableCreate(0, 0)
        testInstance.globalSet("answer")
        assertThat(testInstance.topGet(), equalTo(0))

        testInstance.globalGetTable("answer")
        assertThat(testInstance.type(1), equalTo(5))
        assertThat(testInstance.topGet(), equalTo(1))
    }

    @Test
    fun `Tries to gets global number as table`() {
        testInstance.numberPush(42.0)
        testInstance.globalSet("answer")
        assertThat(testInstance.topGet(), equalTo(0))

        assertThrows<IllegalStateException> { testInstance.globalGetTable("answer") }
            .also { exception ->
                assertThat(
                    exception.message, equalTo("Expected type to be table but was number")
                )
            }

        assertThat(testInstance.topGet(), equalTo(0))
    }


    @Test
    fun `Tries to load global, but does not exist`() {
        testInstance.globalGet("answer")
        assertThat(testInstance.type(1), equalTo(0))
        assertThat(testInstance.topGet(), equalTo(1))
    }

    @Test
    fun `Tries to get global but prevents stack overflow`() {
        testInstance.tableCreate(0, 0)
        testInstance.globalSet("hamal")
        repeat(999999) { testInstance.booleanPush(true) }
        assertThrows<IllegalArgumentException> { testInstance.globalGetTable("hamal") }
            .also { exception -> assertThat(exception.message, equalTo("Prevented stack overflow")) }
    }

}
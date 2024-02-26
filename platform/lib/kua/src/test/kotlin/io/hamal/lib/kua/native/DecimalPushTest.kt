package io.hamal.lib.kua.native

import io.hamal.lib.kua.type.KuaDecimal
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class DecimalPushTest : NativeBaseTest() {
    @Test
    fun `Pushes value on stack`() {
        val result = testInstance.decimalPush(KuaDecimal("23.23"))
        assertThat(result, equalTo(1))
        assertThat(testInstance.topGet(), equalTo(1))
        assertThat(testInstance.decimalGet(1), equalTo(KuaDecimal("23.23")))
    }

    @Test
    fun `Tries to push too many items on the stack limited to 999_999`() {
        repeat(999997) { testInstance.decimalPush(KuaDecimal(it)) }
        assertThrows<IllegalArgumentException> { testInstance.decimalPush(KuaDecimal(-1)) }
            .also { exception -> assertThat(exception.message, equalTo("Prevented stack overflow")) }
    }
}
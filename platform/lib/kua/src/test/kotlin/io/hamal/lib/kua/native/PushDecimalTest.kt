package io.hamal.lib.kua.native

import io.hamal.lib.kua.type.KuaDecimal
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class PushDecimalTest : NativeBaseTest() {
    @Test
    fun `Pushes value on stack`() {
        val result = testInstance.pushDecimal(KuaDecimal("23.23"))
        assertThat(result, equalTo(1))
        assertThat(testInstance.top(), equalTo(1))
        assertThat(testInstance.toDecimal(1), equalTo(KuaDecimal("23.23")))
    }

    @Test
    fun `Tries to push too many items on the stack limited to 999_999`() {
        repeat(999997) { testInstance.pushDecimal(KuaDecimal(it)) }
        assertThrows<IllegalArgumentException> { testInstance.pushDecimal(KuaDecimal(-1)) }
            .also { exception -> assertThat(exception.message, equalTo("Prevented stack overflow")) }
    }
}
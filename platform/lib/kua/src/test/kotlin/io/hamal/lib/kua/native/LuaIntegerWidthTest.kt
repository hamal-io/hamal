package io.hamal.lib.kua.native

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class LuaIntegerWidthTest : NativeBaseTest() {
    @Test
    fun `Loads integer width from lua`() {
        val result = testInstance.luaIntegerWidth()
        assertThat("64bit platform - 8 byte", result, equalTo(8))
    }
}

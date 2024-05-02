package io.hamal.lib.kua.native

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class LuaVersionJsonNumberTest : NativeBaseTest() {
    @Test
    fun `Loads current lua version number`() {
        val result = testInstance.luaVersionNumber()
        assertThat("5.4", result, equalTo(504))
    }
}
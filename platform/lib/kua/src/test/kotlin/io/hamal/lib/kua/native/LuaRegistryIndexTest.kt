package io.hamal.lib.kua.native

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class LuaRegistryIndexTest : NativeBaseTest() {
    @Test
    fun `Loads integer width from lua`() {
        val result = testInstance.luaRegistryIndex()
        assertThat(result, equalTo(-1001000))
    }
}
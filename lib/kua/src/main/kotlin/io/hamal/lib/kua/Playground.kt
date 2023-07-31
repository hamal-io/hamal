package io.hamal.lib.kua

import io.hamal.lib.kua.NativeLoader.Preference.BuildDir


fun main() {
    NativeLoader.load(BuildDir)
    Sandbox().use { sb ->
        sb.load(
            """
            if x == nil then
                error('thats not supposed to happen')
            end
            """.trimIndent()
        )

    }
}

package io.hamal.lib.kua

import io.hamal.lib.kua.NativeLoader.Preference.BuildDir


fun main() {
    NativeLoader.load(BuildDir)
    Sandbox().use { sb ->
        sb.load(
            """
            assert(1 == 1)
            assert(1 == 2)
            """.trimIndent()
        )

    }
}


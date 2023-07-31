package io.hamal.lib.kua

import io.hamal.lib.kua.NativeLoader.Preference.BuildDir


fun main() {
    NativeLoader.load(BuildDir)

    Sandbox().also {
        it.register(Extension("test", listOf()))
    }.use { sb ->
        sb.load(
            """
            local x = does.not.exist
            """.trimIndent()
        )

    }
}


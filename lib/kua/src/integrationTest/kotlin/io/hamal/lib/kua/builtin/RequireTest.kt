package io.hamal.lib.kua.builtin

import io.hamal.lib.kua.NativeLoader
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extension.NewExt
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Paths


internal class ExtensionTest {

    @Test
    fun `Creates a new instance - everytime it gets invoked`() {
        sandbox.load(
            """
            local ext_one = require('test/extension')
            assert( ext_one.some_number == 42 )
            ext_one.some_number = 1337
            
            local ext_two = require('test/extension')
            assert( ext_two.some_number == 42 )
            
            assert( ext_one.some_number == 1337 )
        """.trimIndent()
        )
    }

    private val sandbox = run {
        NativeLoader.load(NativeLoader.Preference.Resources)
        Sandbox().also { sb ->
            sb.register(
                NewExt(
                    name = "test/extension",
                    init = String(Files.readAllBytes(Paths.get("src/integrationTest/resources/test-extension.lua"))),
                    internals = mapOf()
                )
            )
        }
    }
}

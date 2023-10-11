package io.hamal.lib.kua.builtin

import io.hamal.lib.kua.NativeLoader
import io.hamal.lib.kua.NopSandboxContext
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extension.BundleExtension
import org.junit.jupiter.api.Test


internal class ExtensionTest {

    @Test
    fun `Creates a new instance - everytime it gets invoked`() {
        sandbox.load(
            """
            local ext_one = require('test')
            assert( ext_one.some_number == 42 )
            ext_one.some_number = 1337
            
            local ext_two = require('test')
            assert( ext_two.some_number == 42 )
            
            assert( ext_one.some_number == 1337 )
        """.trimIndent()
        )
    }

    private val sandbox = run {
        NativeLoader.load(NativeLoader.Preference.Resources)
        Sandbox(NopSandboxContext()).also { sb ->
            sb.register(
                BundleExtension(
                    name = "test",
                    internals = mapOf()
                )
            )
        }
    }
}

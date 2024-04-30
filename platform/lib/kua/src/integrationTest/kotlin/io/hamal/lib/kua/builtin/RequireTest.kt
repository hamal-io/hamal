package io.hamal.lib.kua.builtin

import io.hamal.lib.kua.NativeLoader
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.SandboxContextNop
import io.hamal.lib.kua.extend.extension.RunnerExtension
import io.hamal.lib.value.ValueCode
import io.hamal.lib.value.ValueString
import org.junit.jupiter.api.Test


internal object ExtensionTest {

    @Test
    fun `Creates a new instance - everytime it gets invoked`() {
        sandbox.codeLoad(
            ValueCode(
                """
            local one = require('test')
            assert( one.some_number == 42 )
            one.some_number = 1337
            
            local two = require('test')
            assert( two.some_number == 42 )
            
            assert( one.some_number == 1337 )
        """.trimIndent()
            )
        )
    }

    private val sandbox = run {
        NativeLoader.load(NativeLoader.Preference.Resources)
        Sandbox(SandboxContextNop).also { sb ->
            sb.register(
                RunnerExtension(
                    name = ValueString("test"),
                    factoryCode = ValueCode(
                        """
                            function extension_create()
                               local export = { some_number = 42 }
                               return export
                            end
                    """.trimIndent()
                    )
                )
            )
        }
    }
}


package io.hamal.lib.kua.builtin

import io.hamal.lib.kua.NativeLoader
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.SandboxContextNop
import io.hamal.lib.kua.extend.plugin.RunnerPlugin
import io.hamal.lib.common.value.ValueCode
import io.hamal.lib.common.value.ValueString
import org.junit.jupiter.api.Test

internal object PluginTest {

    @Test
    fun `Creates a new instance - everytime it gets invoked`() {
        sandbox.codeLoad(
            ValueCode(
                """
            local one = require_plugin('test')
            assert( one.some_number == 42 )
            one.some_number = 1337
            
            local two = require_plugin('test')
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
                RunnerPlugin(
                    name = ValueString("test"),
                    factoryCode = ValueCode(
                        """
                            function plugin_create(internal)
                                local export = { some_number = 42 }
                                return export
                            end
                    """.trimIndent()
                    ),
                    internals = mapOf()
                )
            )
        }
    }
}
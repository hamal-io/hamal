package io.hamal.lib.kua.builtin

import io.hamal.lib.kua.NativeLoader
import io.hamal.lib.kua.NopSandboxContext
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extend.plugin.RunnerPlugin
import io.hamal.lib.kua.extend.extension.RunnerExtension
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test


internal class ExtensionTest {

    @Nested
    inner class ScriptTest {
        @Test
        fun `Creates a new instance - everytime it gets invoked`() {
            sandbox.load(
                """
            local one = require('test')
            assert( one.some_number == 42 )
            one.some_number = 1337
            
            local two = require('test')
            assert( two.some_number == 42 )
            
            assert( one.some_number == 1337 )
        """.trimIndent()
            )
        }

        private val sandbox = run {
            NativeLoader.load(NativeLoader.Preference.Resources)
            Sandbox(NopSandboxContext()).also { sb ->
                sb.register(
                    RunnerExtension(
                        name = "test",
                        factoryCode = """
                            function extension()
                                return function()
                                    local export = { some_number = 42 }
                                    return export
                                end
                            end
                    """.trimIndent()
                    )
                )
            }
        }
    }

    @Nested
    inner class PluginTest {
        @Test
        fun `Creates a new instance - everytime it gets invoked`() {
            sandbox.load(
                """
            local one = require('test')
            assert( one.some_number == 42 )
            one.some_number = 1337
            
            local two = require('test')
            assert( two.some_number == 42 )
            
            assert( one.some_number == 1337 )
        """.trimIndent()
            )
        }

        private val sandbox = run {
            NativeLoader.load(NativeLoader.Preference.Resources)
            Sandbox(NopSandboxContext()).also { sb ->
                sb.register(
                    RunnerPlugin(
                        name = "test",
                        factoryCode = """
                            function extension()
                                local internal = _internal
                                return function()
                                    local export = { some_number = 42 }
                                    return export
                                end
                            end
                    """.trimIndent(),
                        internals = mapOf()
                    )
                )
            }
        }
    }
}

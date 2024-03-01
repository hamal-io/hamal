package io.hamal.lib.kua

import io.hamal.lib.kua.NativeLoader.Preference.Resources
import io.hamal.lib.kua.extend.extension.RunnerExtension
import io.hamal.lib.kua.extend.plugin.RunnerPlugin
import io.hamal.lib.kua.function.Function0In0Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.type.KuaCode
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test


internal class RegisterExtensionTest : BaseSandboxTest() {
    @Test
    fun `Registers an extension and call function`() {
        testInstance.register(
            RunnerExtension(
                name = "some_plugin",
                factoryCode = KuaCode(
                    """
                    function extension_factory_create()
                        return function()
                            local export = { 
                                magic = function() end
                            }
                            return export
                        end
                    end
                """.trimIndent()
                ),
            )
        )

        testInstance.codeLoad(
            KuaCode(
                """
                some_plugin = require('some_plugin')
                some_plugin.magic()
            """.trimIndent()
            )
        )
    }
}


internal class RegisterPluginTest : BaseSandboxTest() {
    @Test
    fun `Registers a plugin and call function`() {
        class TestFunction : Function0In0Out() {
            override fun invoke(ctx: FunctionContext) {
                set = true
            }

            var set = false
        }

        val func = TestFunction()
        testInstance.register(
            RunnerPlugin(
                name = "some_plugin",
                factoryCode = KuaCode(
                    """
                    function plugin_factory_create()
                        local internal = _internal
                        return function()
                            local export = { 
                                magic =  internal.magic
                            }
                            return export
                        end
                    end
                """.trimIndent()
                ),
                internals = mapOf("magic" to func)
            )
        )

        testInstance.codeLoad(
            KuaCode(
                """
                some_plugin = require_plugin('some_plugin')
                some_plugin.magic()
            """.trimIndent()
            )
        )
        assertThat(func.set, equalTo(true))
    }
}


internal sealed class BaseSandboxTest {
    val testInstance = run {
        NativeLoader.load(Resources)
        Sandbox(SandboxContextNop)
    }
}

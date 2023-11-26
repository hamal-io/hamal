package io.hamal.lib.kua

import io.hamal.lib.kua.NativeLoader.Preference.Resources
import io.hamal.lib.kua.extend.plugin.RunnerPlugin
import io.hamal.lib.kua.function.Function0In0Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.type.CodeType
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test


internal class RegisterExtensionTest : BaseSandboxTest() {

    @Test
    fun `Registers a plugin extension and call function`() {
        class TestFunction : Function0In0Out() {
            override fun invoke(ctx: FunctionContext) {
                set = true
            }

            var set = false
        }

        val func = TestFunction()
        testInstance.register(
            RunnerPlugin(
                name = "secret_ext",
                factoryCode = """
                    function extension()
                        local internal = _internal
                        return function()
                            local export = { 
                                magic =  internal.magic
                            }
                            return export
                        end
                    end
                """.trimIndent(),
                internals = mapOf("magic" to func)
            )
        )

        testInstance.load(
            CodeType(
                """
                secret_ext = require('secret_ext')
                secret_ext.magic()
            """.trimIndent()
            )
        )
        assertThat(func.set, equalTo(true))
    }
}


internal sealed class BaseSandboxTest {
    val testInstance = run {
        NativeLoader.load(Resources)
        Sandbox(NopSandboxContext())
    }

    fun verifyStackIsEmpty() {
        assertThat("Stack is empty", testInstance.top, equalTo(0))
    }
}

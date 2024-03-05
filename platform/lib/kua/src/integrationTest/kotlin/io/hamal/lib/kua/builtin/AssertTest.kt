package io.hamal.lib.kua.builtin

import io.hamal.lib.kua.*
import io.hamal.lib.kua.NativeLoader.Preference.Resources
import io.hamal.lib.kua.extend.plugin.RunnerPlugin
import io.hamal.lib.kua.function.Function0In0Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.type.KuaCode
import io.hamal.lib.kua.type.KuaString
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.fail

class AssertTest {

    @Test
    fun `Assertion passes`() {
        sandbox.codeLoad(
            KuaCode(
                """
            assert(1 == 1)
        """.trimIndent()
            )
        )
    }

    @Test
    fun `Assertion fails`() {
        val error = assertThrows<AssertionError> {
            sandbox.codeLoad(
                KuaCode(
                    """
                local provide_answer = function() return 24 end
                
                assert(provide_answer() == 42)
            """.trimIndent()
                )
            )
        }
        assertThat(error.message, equalTo("Line 3: assertion failed!"))
    }

    @Test
    fun `Assertion failure interrupts script execution`() {
        assertThrows<AssertionError> {
            sandbox.codeLoad(
                KuaCode(
                    """
                assert(true == false)
                require('test').call()
                """.trimIndent()
                )
            )
        }
    }

    @Test
    fun `Throws an error if error happen within assert - length of nil`() {
        val error = assertThrows<ScriptError> {
            sandbox.codeLoad(
                KuaCode(
                    """
                local value = nil
                assert(#value == 0)
            """.trimIndent()
                )
            )
        }
        assertThat(
            error.message,
            equalTo("""[string "local value = nil..."]:2: attempt to get length of a nil value (local 'value')""")
        )
    }

    class CallbackFunction : Function0In0Out() {
        override fun invoke(ctx: FunctionContext) {
            fail("Invocation of this callback function not expected")
        }
    }

    private val sandbox = run {
        NativeLoader.load(Resources)
        Sandbox(SandboxContextNop).also {
            it.register(
                RunnerPlugin(
                    KuaString("test"),
                    factoryCode = KuaCode(
                        """
                            function plugin_create(internal)
                                local export = {
                                    call = internal.call
                                 }
                                return export
                            end
                    """.trimIndent()
                    ),
                    internals = mapOf(KuaString("call") to CallbackFunction())
                )
            )
        }
    }
}
package io.hamal.lib.kua.builtin

import io.hamal.lib.kua.*
import io.hamal.lib.kua.NativeLoader.Preference.Resources
import io.hamal.lib.kua.extend.plugin.RunnerPlugin
import io.hamal.lib.kua.function.Function0In0Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.value.ValueCode
import io.hamal.lib.value.ValueString
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.fail

class AssertTest {

    @Test
    fun `Assertion passes`() {
        sandbox.codeLoad(
            ValueCode(
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
                ValueCode(
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
                ValueCode(
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
                ValueCode(
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
                    ValueString("test"),
                    factoryCode = ValueCode(
                        """
                            function plugin_create(internal)
                                local export = {
                                    call = internal.call
                                 }
                                return export
                            end
                    """.trimIndent()
                    ),
                    internals = mapOf(ValueString("call") to CallbackFunction())
                )
            )
        }
    }
}
package io.hamal.lib.kua.builtin

import io.hamal.lib.kua.AssertionError
import io.hamal.lib.kua.Extension
import io.hamal.lib.kua.NativeLoader
import io.hamal.lib.kua.NativeLoader.Preference.Resources
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.function.Function0In0Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.NamedFunctionValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.fail

class AssertTest {

    @Test
    fun `Assertion passes`() {
        sandbox.load(
            """
            assert(1 == 1)
        """.trimIndent()
        )
    }

    @Test
    fun `Assertion fails`() {
        val error = assertThrows<AssertionError> {
            sandbox.load(
            """
                local provide_answer = function() return 24 end
                
                assert(provide_answer() == 42)
            """.trimIndent()
            )
        }
        assertThat(error.message, equalTo("Line 3: assertion failed!"))
    }

    @Test
    fun `Assertion failure interrupts script execution`() {
        assertThrows<AssertionError> {
            sandbox.load(
                """
                assert(true == false)
                test.call()
                """.trimIndent()
            )
        }
    }

    class CallbackFunction : Function0In0Out() {
        override fun invoke(ctx: FunctionContext) {
            fail("Invocation of this callback function not expected")
        }
    }

    private val sandbox = run {
        NativeLoader.load(Resources)
        Sandbox().also {
            it.register(
                Extension(
                    "test",
                    functions = listOf(NamedFunctionValue("call", CallbackFunction()))
                )
            )
        }
    }
}
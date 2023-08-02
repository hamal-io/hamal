package io.hamal.lib.kua.error

import io.hamal.lib.kua.NativeLoader
import io.hamal.lib.kua.NativeLoader.Preference.Resources
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.ScriptError
import io.hamal.lib.kua.extension.NativeExtension
import io.hamal.lib.kua.function.Function0In0Out
import io.hamal.lib.kua.function.FunctionContext
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.fail

class ScriptErrorTest {
    @Test
    fun `Throws an error if script error occurs`() {
        val error = assertThrows<ScriptError> {
            sandbox.load("""local x = does.not.exist""")
        }
        assertThat(error.message, equalTo("[string \"local x = does.not.exist\"]:1: <name> expected near 'not'"))
    }

    @Test
    fun `Script error interrupts execution`() {
        assertThrows<ScriptError> {
            sandbox.load("""local x = does.not.exist; test.call()""")
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
            it.register(NativeExtension("test", mapOf("call" to CallbackFunction())))
        }
    }
}
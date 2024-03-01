package io.hamal.lib.kua.builtin

import io.hamal.lib.kua.NativeLoader
import io.hamal.lib.kua.NativeLoader.Preference.Resources
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.SandboxContextNop
import io.hamal.lib.kua.ScriptError
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

class ErrorTest {

    @Test
    fun `Throws an error`() {
        val error = assertThrows<ScriptError> {
            sandbox.codeLoad(KuaCode("""error("this should not have happened")"""))
        }
        assertThat(
            error.message,
            equalTo("[string \"error(\"this should not have happened\")\"]:1: this should not have happened")
        )
    }

    @Test
    fun `Assertion failure interrupts script execution`() {
        assertThrows<ScriptError> {
            sandbox.codeLoad(
                KuaCode(
                    """
                error("terminate here")
                require('test').call()
                """.trimIndent()
                )
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
        Sandbox(SandboxContextNop).also {
            it.register(
                RunnerPlugin(
                    name = "test",
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
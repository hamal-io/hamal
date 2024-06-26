package io.hamal.lib.kua.error

import io.hamal.lib.kua.NativeLoader
import io.hamal.lib.kua.NativeLoader.Preference.Resources
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.SandboxContextNop
import io.hamal.lib.kua.ErrorInternal
import io.hamal.lib.kua.extend.plugin.RunnerPlugin
import io.hamal.lib.kua.function.Function0In0Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.common.value.ValueCode
import io.hamal.lib.common.value.ValueString
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.fail

class ErrorInternalTest {
    @Test
    fun `Throws an error if script error occurs`() {
        val error = assertThrows<ErrorInternal> {
            sandbox.codeLoad(ValueCode("""local x = does.not.exist"""))
        }
        assertThat(error.message, equalTo("[string \"local x = does.not.exist\"]:1: <name> expected near 'not'"))
    }

    @Test
    fun `Script error interrupts execution`() {
        assertThrows<ErrorInternal> {
            sandbox.codeLoad(ValueCode("""local x = does.not.exist; require('test').call()"""))
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
                    name = ValueString("test"),
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
package io.hamal.lib.kua.extension

import io.hamal.lib.kua.NativeLoader
import io.hamal.lib.kua.NativeLoader.Preference.Resources
import io.hamal.lib.kua.NopSandboxContext
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.function.Function0In0Out
import io.hamal.lib.kua.function.FunctionContext
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class ExtensionTest {

    @Test
    fun `Invokes function of test extension`() {
        sandbox.load(
            """
            local test_extension = require('test')
            for x=1,10 do
                test_extension.call()
            end
        """.trimIndent()
        )
        assertThat(TestCall0In0OutFunction.counter, equalTo(10))
    }

    @Test
    fun `Able to access fields of extension`() {
        sandbox.load(
            """
            local test_extension = require('test')
            assert( test_extension.some_number == 42 )
            assert( test_extension.some_boolean == true)
        """.trimIndent()
        )
    }

    private val sandbox = run {
        NativeLoader.load(Resources)
        Sandbox(NopSandboxContext()).also { sb ->
            sb.register(
                ScriptExtension(
                    name = "test",
                    internals = mapOf(
                        "test_extension_call" to TestCall0In0OutFunction
                    )
                )
            )
        }
    }
}

internal object TestCall0In0OutFunction : Function0In0Out() {
    override fun invoke(ctx: FunctionContext) {
        counter++
    }

    var counter = 0
}
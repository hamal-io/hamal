package io.hamal.lib.kua.capability

import io.hamal.lib.kua.NativeLoader
import io.hamal.lib.kua.NativeLoader.Preference.Resources
import io.hamal.lib.kua.NopSandboxContext
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extension.unsafe.RunnerUnsafeExtension
import io.hamal.lib.kua.function.Function0In0Out
import io.hamal.lib.kua.function.FunctionContext
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class ExtensionTest {

    @Test
    fun `Invokes function of test capability`() {
        sandbox.load(
            """
            local test = require('test')
            for x=1,10 do
                test.call()
            end
        """.trimIndent()
        )
        assertThat(TestCall0In0OutFunction.counter, equalTo(10))
    }

    @Test
    fun `Able to access fields of capability`() {
        sandbox.load(
            """
            local test = require('test')
            assert( test.some_number == 42 )
            assert( test.some_boolean == true)
        """.trimIndent()
        )
    }

    private val sandbox = run {
        NativeLoader.load(Resources)
        Sandbox(NopSandboxContext()).also { sb ->
            sb.register(
                RunnerUnsafeExtension(
                    name = "test",
                    factoryCode = """
                            function extension()
                                local internal = _internal
                                return function()
                                    local export = {
                                        call = internal.test_call,
                                        some_number = 42,
                                        some_boolean = true
                                     }
                                    return export
                                end
                            end
                    """.trimIndent(),
                    internals = mapOf(
                        "test_call" to TestCall0In0OutFunction
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
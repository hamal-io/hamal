package io.hamal.lib.kua.extend

import io.hamal.lib.kua.NativeLoader
import io.hamal.lib.kua.NativeLoader.Preference.Resources
import io.hamal.lib.kua.SandboxContextNop
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extend.plugin.RunnerPlugin
import io.hamal.lib.kua.function.Function0In0Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.type.KuaCode
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class PluginTest {

    @Test
    fun `Invokes function of test capability`() {
        sandbox.codeLoad(
            KuaCode(
                """
            local test = require_plugin('test')
            for x=1,10 do
                test.call()
            end
        """.trimIndent()
            )
        )
        assertThat(TestCall0In0OutFunction.counter, equalTo(10))
    }

    @Test
    fun `Able to access fields of capability`() {
        sandbox.codeLoad(
            KuaCode(
                """
            local test = require_plugin('test')
            assert( test.some_number == 42 )
            assert( test.some_boolean == true)
        """.trimIndent()
            )
        )
    }

    private val sandbox = run {
        NativeLoader.load(Resources)
        Sandbox(SandboxContextNop).also { sb ->
            sb.register(
                RunnerPlugin(
                    name = "test",
                    factoryCode = KuaCode(
                        """
                            function plugin()
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
                    """.trimIndent()
                    ),
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
package io.hamal.lib.kua.type

import io.hamal.lib.kua.NativeLoader
import io.hamal.lib.kua.NativeLoader.Preference.Resources
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.SandboxContextNop
import io.hamal.lib.kua.extend.plugin.RunnerPlugin
import io.hamal.lib.kua.function.*
import io.hamal.lib.value.ValueCode
import io.hamal.lib.value.Value
import io.hamal.lib.value.ValueNumber
import io.hamal.lib.value.ValueString
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Test

internal class KuaErrorTest {

    @Test
    fun `Function value returns error value`() {
        val messageCaptor = Captor()

        sandbox.register(
            RunnerPlugin(
                name = ValueString("test"),
                factoryCode = ValueCode(
                    """
                    function plugin_create(internal)
                        local export = { 
                            error =  internal.error,
                            message_captor =  internal.message_captor,
                            assert_metatable =  internal.assert_metatable,
                        }
                        return export
                    end
                """.trimIndent()
                ),
                internals = mapOf(
                    ValueString("error") to FunctionReturnsError(),
                    ValueString("message_captor") to messageCaptor,
                    ValueString("assert_metatable") to AssertMetatable
                )
            )
        )

        sandbox.codeLoad(
            ValueCode(
                """
            test = require_plugin('test')
            local err = test.error()
            test.message_captor(err.message)
               
            local mtbl = getmetatable(err)
            test.assert_metatable(mtbl)
        """.trimIndent()
            )
        )

        assertThat(messageCaptor.result, equalTo(ValueString("Sometimes an error can be a good thing")))
    }


    private object AssertMetatable : Function1In0Out<KuaTable>(
        FunctionInput1Schema(KuaTable::class)
    ) {
        override fun invoke(ctx: FunctionContext, arg1: KuaTable) {
            assertThat(arg1.getNumber("__type_id"), equalTo(ValueNumber(10)))
            assertThat(arg1.getString("__typename"), equalTo(ValueString("error")))
        }
    }

    private class FunctionReturnsError : Function0In1Out<KuaError>(
        FunctionOutput1Schema(KuaError::class)
    ) {
        override fun invoke(ctx: FunctionContext): KuaError {
            return KuaError("Sometimes an error can be a good thing")
        }
    }

    private class Captor : Function1In0Out<Value>(
        FunctionInput1Schema(Value::class)
    ) {
        override fun invoke(ctx: FunctionContext, arg1: Value) {
            result = arg1
        }

        var result: Value? = null
    }

    private val sandbox = run {
        NativeLoader.load(Resources)
        Sandbox(SandboxContextNop)
    }
}
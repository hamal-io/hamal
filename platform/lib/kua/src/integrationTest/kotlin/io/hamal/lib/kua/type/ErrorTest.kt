package io.hamal.lib.kua.type

import io.hamal.lib.kua.NativeLoader
import io.hamal.lib.kua.NativeLoader.Preference.Resources
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.SandboxContextNop
import io.hamal.lib.kua.extend.plugin.RunnerPlugin
import io.hamal.lib.kua.function.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

internal class KuaErrorTest {

    @Test
    fun `Function value returns error value`() {
        val messageCaptor = Captor()

        sandbox.register(
            RunnerPlugin(
                name = KuaString("test"),
                factoryCode = KuaCode(
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
                    KuaString("error") to FunctionReturnsError(),
                    KuaString("message_captor") to messageCaptor,
                    KuaString("assert_metatable") to AssertMetatable
                )
            )
        )

        sandbox.codeLoad(
            KuaCode(
                """
            test = require_plugin('test')
            local err = test.error()
            test.message_captor(err.message)
               
            local mtbl = getmetatable(err)
            test.assert_metatable(mtbl)
        """.trimIndent()
            )
        )

        assertThat(messageCaptor.result, equalTo(KuaString("Sometimes an error can be a good thing")))
    }

    @Test
    @Disabled
    fun `Tries to invoke function without argument`() {
        val errorCaptor = Captor()

        sandbox.register(
            RunnerPlugin(
                name = KuaString("test"),
                factoryCode = KuaCode(
                    """
                    function plugin_create(internal)
                        local export = { 
                            call =  internal.call,
                            captor =  internal.captor,
                            assert_metatable =  internal.assert_metatable,
                        }
                        return export
                    end
                """.trimIndent()
                ),
                internals = mapOf(
                    KuaString("call") to FunctionNeverInvoked(),
                    KuaString("captor") to errorCaptor,
                    KuaString("assert_metatable") to AssertMetatable
                )
            )
        )

        sandbox.codeLoad(
            KuaCode(
                """
            local err = test.call()
            test.captor(err)
        """.trimIndent()
            )
        )

        assertThat(errorCaptor.result, equalTo(KuaError("Sometimes an error can be a good thing")))
    }


    private object AssertMetatable : Function1In0Out<KuaTable>(
        FunctionInput1Schema(KuaTable::class)
    ) {
        override fun invoke(ctx: FunctionContext, arg1: KuaTable) {
            assertThat(arg1.getNumber("__type_id"), equalTo(KuaNumber(10)))
            assertThat(arg1.getString("__typename"), equalTo(KuaString("error")))
        }
    }

    private class FunctionReturnsError : Function0In1Out<KuaError>(
        FunctionOutput1Schema(KuaError::class)
    ) {
        override fun invoke(ctx: FunctionContext): KuaError {
            return KuaError("Sometimes an error can be a good thing")
        }
    }


    private class FunctionNeverInvoked : Function1In0Out<KuaNumber>(
        FunctionInput1Schema(KuaNumber::class)
    ) {
        override fun invoke(ctx: FunctionContext, arg1: KuaNumber) {
            TODO("Not yet implemented")
        }
    }

    private class Captor : Function1In0Out<KuaType>(
        FunctionInput1Schema(KuaType::class)
    ) {
        override fun invoke(ctx: FunctionContext, arg1: KuaType) {
            result = arg1
        }

        var result: KuaType? = null
    }

    private val sandbox = run {
        NativeLoader.load(Resources)
        Sandbox(SandboxContextNop)
    }
}
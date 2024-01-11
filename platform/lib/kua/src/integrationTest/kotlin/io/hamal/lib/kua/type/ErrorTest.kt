package io.hamal.lib.kua.type

import io.hamal.lib.kua.NativeLoader
import io.hamal.lib.kua.NativeLoader.Preference.Resources
import io.hamal.lib.kua.NopSandboxContext
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extend.plugin.RunnerPlugin
import io.hamal.lib.kua.function.*
import io.hamal.lib.kua.table.TableProxyMap
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
                name = "test",
                factoryCode = """
                    function plugin()
                        local internal = _internal
                        return function()
                            local export = { 
                                error =  internal.error,
                                message_captor =  internal.message_captor,
                                assert_metatable =  internal.assert_metatable,
                            }
                            return export
                        end
                    end
                """.trimIndent(),
                internals = mapOf(
                    "error" to FunctionReturnsError(),
                    "message_captor" to messageCaptor,
                    "assert_metatable" to AssertMetatable
                )
            )
        )

        sandbox.load(
            """
            test = require_plugin('test')
            local err = test.error()
            test.message_captor(err.message)
               
            local mtbl = getmetatable(err)
            test.assert_metatable(mtbl)
        """.trimIndent()
        )

        assertThat(messageCaptor.result, equalTo(KuaAny(KuaString("Sometimes an error can be a good thing"))))
    }

    @Test
    @Disabled
    fun `Tries to invoke function without argument`() {
        val errorCaptor = Captor()

        sandbox.register(
            RunnerPlugin(
                name = "test",
                factoryCode = """
                    function plugin()
                        local internal = _internal
                        return function()
                            local export = { 
                                call =  internal.call,
                                captor =  internal.captor,
                                assert_metatable =  internal.assert_metatable,
                            }
                            return export
                        end
                    end
                """.trimIndent(),
                internals = mapOf(
                    "call" to FunctionNeverInvoked(),
                    "captor" to errorCaptor,
                    "assert_metatable" to AssertMetatable
                )
            )
        )

        sandbox.load(
            """
            local err = test.call()
            test.captor(err)
        """.trimIndent()
        )

        assertThat(errorCaptor.result, equalTo(KuaAny(KuaError("Sometimes an error can be a good thing"))))
    }


    private object AssertMetatable : Function1In0Out<TableProxyMap>(
        FunctionInput1Schema(TableProxyMap::class)
    ) {
        override fun invoke(ctx: FunctionContext, arg1: TableProxyMap) {
            assertThat(arg1.getInt("__type_id"), equalTo(10))
            assertThat(arg1.getString("__typename"), equalTo("error"))
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

    private class Captor : Function1In0Out<KuaAny>(
        FunctionInput1Schema(KuaAny::class)
    ) {
        override fun invoke(ctx: FunctionContext, arg1: KuaAny) {
            result = arg1
        }

        var result: KuaAny? = null
    }

    private val sandbox = run {
        NativeLoader.load(Resources)
        Sandbox(NopSandboxContext())
    }
}
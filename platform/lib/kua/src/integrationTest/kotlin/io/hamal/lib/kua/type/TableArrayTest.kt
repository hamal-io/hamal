package io.hamal.lib.kua.type

import io.hamal.lib.kua.*
import io.hamal.lib.kua.function.Function0In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionOutput2Schema
import org.junit.jupiter.api.Test

internal class KuaTableArrayTest {

    @Test
    fun `Array as function result`() {
        sandbox.use { sandbox ->
            sandbox.registerGlobalFunction("func",
                object :
                    Function0In2Out<KuaError, KuaTableArray>(
                        FunctionOutput2Schema(
                            KuaError::class,
                            KuaTableArray::class
                        )
                    ) {
                    override fun invoke(ctx: FunctionContext): Pair<KuaError?, KuaTableArray?> {
                        return null to ctx.toArray(listOf(KuaString("A")))
                    }
                }
            )

            sandbox.load(
                """
                local err, array = func()
                assert(err == nil)
                assert(array[1] == "A")
            """.trimIndent()
            )
        }
    }

    @Test
    fun `Nested array as function result`() {
        sandbox.use { sandbox ->
            sandbox.registerGlobalFunction("func",
                object :
                    Function0In2Out<KuaError, KuaTableArray>(
                        FunctionOutput2Schema(
                            KuaError::class,
                            KuaTableArray::class
                        )
                    ) {
                    override fun invoke(ctx: FunctionContext): Pair<KuaError?, KuaTableArray?> {
                        return null to sandbox.toArray(
                            listOf(
                                KuaString("A"),
                                sandbox.toArray(
                                    listOf(
                                        KuaNumber(42)
                                    )
                                )
                            )
                        )
                    }
                }
            )

            sandbox.load(
                """
                local err, table = func()
                assert(err == nil)
                assert(table[1] == "A")
                assert(table[2][1] == 42)
            """.trimIndent()
            )
        }
    }

    private val sandbox = run {
        NativeLoader.load(NativeLoader.Preference.Resources)
        Sandbox(NopSandboxContext())
    }
}
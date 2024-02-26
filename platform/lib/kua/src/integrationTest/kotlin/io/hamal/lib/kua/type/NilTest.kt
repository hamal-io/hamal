package io.hamal.lib.kua.type

import io.hamal.lib.kua.NativeLoader
import io.hamal.lib.kua.NopSandboxContext
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.function.*
import io.hamal.lib.kua.registerGlobalFunction
import org.junit.jupiter.api.Test

internal class KuaNilTest {

    @Test
    fun `Single result it nil`() {
        sandbox.use { sandbox ->
            sandbox.registerGlobalFunction("func",
                object : Function0In1Out<KuaNumber>(FunctionOutput1Schema(KuaNumber::class)) {
                    override fun invoke(ctx: FunctionContext): KuaNumber? {
                        return null
                    }
                }
            )

            sandbox.load(
                """
                local result = func()
                assert(result == nil)
            """.trimIndent()
            )
        }
    }

    @Test
    fun `First result is nil`() {
        sandbox.use { sandbox ->
            sandbox.registerGlobalFunction("func",
                object : Function0In2Out<KuaTable, KuaTable>(
                    FunctionOutput2Schema(
                        KuaTable::class,
                        KuaTable::class
                    )
                ) {
                    override fun invoke(ctx: FunctionContext): Pair<KuaTable?, KuaTable?> {
                        return null to ctx.tableCreateMap(0)
                    }
                }
            )

            sandbox.load(
                """
                local x, y = func()
                assert(x == nil)
                assert(#y == 0)
            """.trimIndent()
            )
        }
    }

    @Test
    fun `Second result is nil`() {
        sandbox.use { sandbox ->
            sandbox.registerGlobalFunction("func",
                object :
                    Function0In2Out<KuaTable, KuaTable>(
                        FunctionOutput2Schema(
                            KuaTable::class,
                            KuaTable::class
                        )
                    ) {
                    override fun invoke(ctx: FunctionContext): Pair<KuaTable?, KuaTable?> {
                        return ctx.tableCreateArray(0) to null
                    }
                })

            sandbox.load(
                """
                local x, y = func()
                assert(#x == 0)
                assert(y == nil)
            """.trimIndent()
            )
        }
    }

    @Test
    fun `Both results are nil`() {
        val func =
            object : Function0In2Out<KuaError, KuaTable>(
                FunctionOutput2Schema(
                    KuaError::class,
                    KuaTable::class
                )
            ) {
                override fun invoke(ctx: FunctionContext): Pair<KuaError?, KuaTable?> {
                    return null to null
                }
            }

        sandbox.use { sandbox ->
            sandbox.registerGlobalFunction("func", func)
            sandbox.load(
                """
                local x, y = func()
                assert(x == nil)
                assert(y == nil)
            """.trimIndent()
            )
        }
    }

    private val sandbox = run {
        NativeLoader.load(NativeLoader.Preference.Resources)
        Sandbox(NopSandboxContext())
    }
}
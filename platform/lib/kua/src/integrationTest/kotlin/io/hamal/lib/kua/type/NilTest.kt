package io.hamal.lib.kua.type

import io.hamal.lib.kua.NativeLoader
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.SandboxContextNop
import io.hamal.lib.kua.function.*
import io.hamal.lib.kua.registerGlobalFunction
import io.hamal.lib.value.ValueNumber
import org.junit.jupiter.api.Test

internal class ValueNilTest {

    @Test
    fun `Single result it nil`() {
        sandbox.use { sandbox ->
            sandbox.registerGlobalFunction("func",
                object : Function0In1Out<ValueNumber>(FunctionOutput1Schema(ValueNumber::class)) {
                    override fun invoke(ctx: FunctionContext): ValueNumber? {
                        return null
                    }
                }
            )

            sandbox.codeLoad(
                KuaCode(
                    """
                local result = func()
                assert(result == nil)
            """.trimIndent()
                )
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
                        return null to ctx.tableCreate()
                    }
                }
            )

            sandbox.codeLoad(
                KuaCode(
                    """
                local x, y = func()
                assert(x == nil)
                assert(#y == 0)
            """.trimIndent()
                )
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
                        return ctx.tableCreate() to null
                    }
                })

            sandbox.codeLoad(
                KuaCode(
                    """
                local x, y = func()
                assert(#x == 0)
                assert(y == nil)
            """.trimIndent()
                )
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
            sandbox.codeLoad(
                KuaCode(
                    """
                local x, y = func()
                assert(x == nil)
                assert(y == nil)
            """.trimIndent()
                )
            )
        }
    }

    private val sandbox = run {
        NativeLoader.load(NativeLoader.Preference.Resources)
        Sandbox(SandboxContextNop)
    }
}
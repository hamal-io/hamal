package io.hamal.lib.kua.type

import io.hamal.lib.kua.*
import io.hamal.lib.kua.function.Function0In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.value.ValueNumber
import io.hamal.lib.value.ValueString
import org.junit.jupiter.api.Test

internal class KuaTableTest {

    @Test
    fun `Table as function result`() {
        sandbox.use { sandbox ->
            sandbox.registerGlobalFunction("func",
                object : Function0In2Out<KuaError, KuaTable>(FunctionOutput2Schema(KuaError::class, KuaTable::class)) {
                    override fun invoke(ctx: FunctionContext): Pair<KuaError?, KuaTable?> {
                        return null to ctx.tableCreate(ValueString("id") to ValueString("A"))
                    }
                }
            )
            sandbox.codeLoad(
                KuaCode(
                    """
                local err, table = func()
                assert(err == nil)
                assert(table.id == "A")
            """.trimIndent()
                )
            )
        }
    }

    @Test
    fun `Table (array)  as function result`() {
        sandbox.use { sandbox ->
            sandbox.registerGlobalFunction("func",
                object : Function0In2Out<KuaError, KuaTable>(FunctionOutput2Schema(KuaError::class, KuaTable::class)) {
                    override fun invoke(ctx: FunctionContext): Pair<KuaError?, KuaTable?> {
                        return null to ctx.tableCreate(listOf(ValueString("A")))
                    }
                }
            )

            sandbox.codeLoad(
                KuaCode(
                    """
                local err, table = func()
                assert(err == nil)
                assert(table[1] == "A")
            """.trimIndent()
                )
            )
        }
    }

    @Test
    fun `Nested table as function result`() {
        sandbox.use { sandbox ->
            sandbox.registerGlobalFunction("func",
                object : Function0In2Out<KuaError, KuaTable>(FunctionOutput2Schema(KuaError::class, KuaTable::class)) {
                    override fun invoke(ctx: FunctionContext): Pair<KuaError?, KuaTable?> {
                        return null to sandbox.tableCreate(
                            ValueString("id") to ValueString("A"),
                            ValueString("level_one") to sandbox.tableCreate(ValueString("answer") to ValueNumber(42))
                        )
                    }
                }
            )

            sandbox.codeLoad(
                KuaCode(
                    """
                local err, table = func()
                assert(err == nil)
                assert(table.id == "A")
                assert(table.level_one.answer == 42)
            """.trimIndent()
                )
            )
        }
    }

    @Test
    fun `Nested table (array) as function result`() {
        sandbox.use { sandbox ->
            sandbox.registerGlobalFunction("func",
                object : Function0In2Out<KuaError, KuaTable>(FunctionOutput2Schema(KuaError::class, KuaTable::class)) {
                    override fun invoke(ctx: FunctionContext): Pair<KuaError?, KuaTable?> {
                        return null to ctx.tableCreate(
                            listOf(
                                ValueString("A"),
                                ctx.tableCreate(listOf(ValueNumber(42)))
                            )
                        )
                    }
                }
            )

            sandbox.codeLoad(
                KuaCode(
                    """
                local err, table = func()
                assert(err == nil)
                assert(table[1] == "A")
                assert(table[2][1] == 42)
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
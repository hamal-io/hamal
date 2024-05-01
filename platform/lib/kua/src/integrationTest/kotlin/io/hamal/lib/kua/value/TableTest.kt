package io.hamal.lib.kua.value

import io.hamal.lib.kua.*
import io.hamal.lib.kua.function.Function0In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.value.ValueCode
import io.hamal.lib.value.ValueError
import io.hamal.lib.value.ValueNumber
import io.hamal.lib.value.ValueString
import org.junit.jupiter.api.Test

internal class KuaTableTest {

    @Test
    fun `Table as function result`() {
        sandbox.use { sandbox ->
            sandbox.registerGlobalFunction("func",
                object :
                    Function0In2Out<ValueError, KuaTable>(FunctionOutput2Schema(ValueError::class, KuaTable::class)) {
                    override fun invoke(ctx: FunctionContext): Pair<ValueError?, KuaTable?> {
                        return null to ctx.tableCreate(ValueString("id") to ValueString("A"))
                    }
                }
            )
            sandbox.codeLoad(
                ValueCode(
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
                object :
                    Function0In2Out<ValueError, KuaTable>(FunctionOutput2Schema(ValueError::class, KuaTable::class)) {
                    override fun invoke(ctx: FunctionContext): Pair<ValueError?, KuaTable?> {
                        return null to ctx.tableCreate(listOf(ValueString("A")))
                    }
                }
            )

            sandbox.codeLoad(
                ValueCode(
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
                object :
                    Function0In2Out<ValueError, KuaTable>(FunctionOutput2Schema(ValueError::class, KuaTable::class)) {
                    override fun invoke(ctx: FunctionContext): Pair<ValueError?, KuaTable?> {
                        return null to sandbox.tableCreate(
                            ValueString("id") to ValueString("A"),
                            ValueString("level_one") to sandbox.tableCreate(ValueString("answer") to ValueNumber(42))
                        )
                    }
                }
            )

            sandbox.codeLoad(
                ValueCode(
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
                object :
                    Function0In2Out<ValueError, KuaTable>(FunctionOutput2Schema(ValueError::class, KuaTable::class)) {
                    override fun invoke(ctx: FunctionContext): Pair<ValueError?, KuaTable?> {
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
                ValueCode(
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
package io.hamal.lib.kua.type

import io.hamal.lib.kua.*
import io.hamal.lib.kua.function.Function0In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionOutput2Schema
import org.junit.jupiter.api.Test

internal class KuaTableMaTest {

    @Test
    fun `Map as function result`() {
        sandbox.use { sandbox ->
            sandbox.registerGlobalFunction("func",
                object :
                    Function0In2Out<KuaError, KuaTable>(FunctionOutput2Schema(KuaError::class, KuaTable::class)) {
                    override fun invoke(ctx: FunctionContext): Pair<KuaError?, KuaTable?> {
                        return null to ctx.createTable("id" to KuaString("A"))
                    }
                }
            )

            sandbox.load(
                """
                local err, table = func()
                assert(err == nil)
                assert(table.id == "A")
            """.trimIndent()
            )
        }
    }

    @Test
    fun `Nested map as function result`() {
        sandbox.use { sandbox ->
            sandbox.registerGlobalFunction("func",
                object :
                    Function0In2Out<KuaError, KuaTable>(FunctionOutput2Schema(KuaError::class, KuaTable::class)) {
                    override fun invoke(ctx: FunctionContext): Pair<KuaError?, KuaTable?> {
                        return null to sandbox.createTable(
                            "id" to KuaString("A"),
                            "level_one" to sandbox.createTable("answer" to KuaNumber(42))
                        )
                    }
                }
            )

            sandbox.load(
                """
                local err, table = func()
                assert(err == nil)
                assert(table.id == "A")
                assert(table.level_one.answer == 42)
            """.trimIndent()
            )
        }
    }

    private val sandbox = run {
        NativeLoader.load(NativeLoader.Preference.Resources)
        Sandbox(NopSandboxContext())
    }
}
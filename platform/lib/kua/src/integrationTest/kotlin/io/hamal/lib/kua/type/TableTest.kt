package io.hamal.lib.kua.type

import io.hamal.lib.kua.NativeLoader
import io.hamal.lib.kua.NopSandboxContext
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.function.Function0In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.registerGlobalFunction
import org.junit.jupiter.api.Test

internal class KuaTableTest {

    @Test
    fun `Map as function result`() {
        sandbox.use { sandbox ->
            sandbox.registerGlobalFunction("func",
                object : Function0In2Out<KuaError, KuaTable>(FunctionOutput2Schema(KuaError::class, KuaTable::class)) {
                    override fun invoke(ctx: FunctionContext): Pair<KuaError?, KuaTable?> {
                        return null to KuaTable.Map(mutableMapOf("id" to KuaString("A")))
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
                object : Function0In2Out<KuaError, KuaTable>(FunctionOutput2Schema(KuaError::class, KuaTable::class)) {
                    override fun invoke(ctx: FunctionContext): Pair<KuaError?, KuaTable?> {
                        return null to KuaTable.Map(
                            "id" to KuaString("A"),
                            "level_one" to KuaTable.Map(mutableMapOf("answer" to KuaNumber(42)))
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
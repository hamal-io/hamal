package io.hamal.lib.kua.type

import io.hamal.lib.kua.NativeLoader
import io.hamal.lib.kua.NopSandboxContext
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.function.Function0In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.registerGlobalFunction
import org.junit.jupiter.api.Test

internal class MapTypeTest {

    @Test
    fun `Map as function result`() {
        sandbox.use { sandbox ->
            sandbox.registerGlobalFunction("func",
                object : Function0In2Out<ErrorType, MapType>(FunctionOutput2Schema(ErrorType::class, MapType::class)) {
                    override fun invoke(ctx: FunctionContext): Pair<ErrorType?, MapType?> {
                        return null to MapType(mutableMapOf("id" to StringType("A")))
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
                object : Function0In2Out<ErrorType, MapType>(FunctionOutput2Schema(ErrorType::class, MapType::class)) {
                    override fun invoke(ctx: FunctionContext): Pair<ErrorType?, MapType?> {
                        return null to MapType(
                            mutableMapOf(
                                "id" to StringType("A"),
                                "level_one" to MapType(mutableMapOf("answer" to NumberType(42)))
                            )
                        )
                    }
                }
            )

            sandbox.load("""
                local err, table = func()
                assert(err == nil)
                assert(table.id == "A")
                assert(table.level_one.answer == 42)
            """.trimIndent())
        }
    }

    private val sandbox = run {
        NativeLoader.load(NativeLoader.Preference.Resources)
        Sandbox(NopSandboxContext())
    }
}
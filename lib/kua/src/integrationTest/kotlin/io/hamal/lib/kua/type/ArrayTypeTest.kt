package io.hamal.lib.kua.type

import io.hamal.lib.kua.NativeLoader
import io.hamal.lib.kua.NopSandboxContext
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.function.Function0In2Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionOutput2Schema
import io.hamal.lib.kua.registerGlobalFunction
import org.junit.jupiter.api.Test

internal class ArrayTypeTest {

    @Test
    fun `Array as function result`() {
        sandbox.use { sandbox ->
            sandbox.registerGlobalFunction("func",
                object :
                    Function0In2Out<ErrorType, ArrayType>(FunctionOutput2Schema(ErrorType::class, ArrayType::class)) {
                    override fun invoke(ctx: FunctionContext): Pair<ErrorType?, ArrayType?> {
                        return null to ArrayType(mutableMapOf(1 to StringType("A")))
                    }
                }
            )

            sandbox.load(
                """
                local err, table = func()
                assert(err == nil)
                assert(table[1] == "A")
            """.trimIndent()
            )
        }
    }

    @Test
    fun `Nested array as function result`() {
        sandbox.use { sandbox ->
            sandbox.registerGlobalFunction("func",
                object :
                    Function0In2Out<ErrorType, ArrayType>(FunctionOutput2Schema(ErrorType::class, ArrayType::class)) {
                    override fun invoke(ctx: FunctionContext): Pair<ErrorType?, ArrayType?> {
                        return null to ArrayType(
                            mutableMapOf(
                                1 to StringType("A"),
                                2 to ArrayType(mutableMapOf(1 to NumberType(42)))
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
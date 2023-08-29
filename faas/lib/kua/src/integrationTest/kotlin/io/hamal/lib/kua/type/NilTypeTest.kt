package io.hamal.lib.kua.type

import io.hamal.lib.kua.NativeLoader
import io.hamal.lib.kua.NopSandboxContext
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.function.*
import io.hamal.lib.kua.registerGlobalFunction
import org.junit.jupiter.api.Test

internal class NilTypeTest {

    @Test
    fun `Single result it nil`() {
        sandbox.use { sandbox ->
            sandbox.registerGlobalFunction("func",
                object : Function0In1Out<NumberType>(FunctionOutput1Schema(NumberType::class)) {
                    override fun invoke(ctx: FunctionContext): NumberType? {
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
                object : Function0In2Out<ArrayType, MapType>(FunctionOutput2Schema(ArrayType::class, MapType::class)) {
                    override fun invoke(ctx: FunctionContext): Pair<ArrayType?, MapType?> {
                        return null to MapType()
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
                    Function0In2Out<ArrayType, MapType>(FunctionOutput2Schema(ArrayType::class, MapType::class)) {
                    override fun invoke(ctx: FunctionContext): Pair<ArrayType?, MapType?> {
                        return ArrayType() to null
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
            object : Function0In2Out<ErrorType, MapType>(FunctionOutput2Schema(ErrorType::class, MapType::class)) {
                override fun invoke(ctx: FunctionContext): Pair<ErrorType?, MapType?> {
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
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
                object : Function0In2Out<KuaArray, KuaMap>(
                    FunctionOutput2Schema(
                        KuaArray::class,
                        KuaMap::class
                    )
                ) {
                    override fun invoke(ctx: FunctionContext): Pair<KuaArray?, KuaMap?> {
                        return null to KuaMap()
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
                    Function0In2Out<KuaArray, KuaMap>(
                        FunctionOutput2Schema(
                            KuaArray::class,
                            KuaMap::class
                        )
                    ) {
                    override fun invoke(ctx: FunctionContext): Pair<KuaArray?, KuaMap?> {
                        return KuaArray() to null
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
            object : Function0In2Out<KuaError, KuaMap>(
                FunctionOutput2Schema(
                    KuaError::class,
                    KuaMap::class
                )
            ) {
                override fun invoke(ctx: FunctionContext): Pair<KuaError?, KuaMap?> {
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
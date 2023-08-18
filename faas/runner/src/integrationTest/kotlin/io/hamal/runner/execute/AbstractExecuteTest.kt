package io.hamal.runner.execute

import io.hamal.lib.kua.NativeLoader
import io.hamal.lib.kua.NativeLoader.Preference.Resources
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.SandboxContext
import io.hamal.lib.kua.extension.NativeExtension
import io.hamal.lib.kua.function.Function1In0Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.kua.type.Type
import io.hamal.runner.TestConnector
import io.hamal.runner.config.SandboxFactory

internal abstract class AbstractExecuteTest(
    val stringCaptor: StringCaptor = StringCaptor()
) {

    fun createTestExecutor(vararg testExtensions: Pair<String, Type>) = DefaultExecutor(
        TestConnector(), object : SandboxFactory {
            override fun create(ctx: SandboxContext): Sandbox {
                NativeLoader.load(Resources)
                return Sandbox(ctx).also {
                    it.register(
                        NativeExtension(
                            name = "test",
                            values = mapOf(
                                "capture_string" to stringCaptor
                            ).plus(testExtensions)
                        )
                    )
                }
            }
        }
    )

    class StringCaptor(
        var result: String? = null
    ) : Function1In0Out<StringType>(FunctionInput1Schema(StringType::class)) {
        override fun invoke(ctx: FunctionContext, arg1: StringType) {
            result = arg1.value
        }
    }
}
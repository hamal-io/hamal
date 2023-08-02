package io.hamal.extension.std.log

import io.hamal.lib.kua.extension.NativeExtension
import io.hamal.lib.kua.extension.NativeExtensionFactory
import io.hamal.lib.kua.function.Function1In0Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.value.AnyValue

class LogExtensionFactory : NativeExtensionFactory {
    override fun create(): NativeExtension {
        return NativeExtension(
            name = "log",
            values = mapOf(
                "info" to InfoFunction()
            )
        )
    }
}

class InfoFunction : Function1In0Out<AnyValue>(
    FunctionInput1Schema(AnyValue::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: AnyValue) {
        println("INFO: ${arg1.value}")
    }
}

package io.hamal.extension.std.log

import io.hamal.lib.kua.Extension
import io.hamal.lib.kua.ExtensionFactory
import io.hamal.lib.kua.function.Function1In0Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.function.NamedFunctionValue
import io.hamal.lib.kua.value.AnyValue

class LogExtensionFactory : ExtensionFactory {
    override fun create(): Extension {
        return Extension(
            name = "log",
            functions = listOf(
                NamedFunctionValue(
                    name = "info",
                    function = InfoFunction()
                )
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

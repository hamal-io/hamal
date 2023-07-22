package io.hamal.agent.extension.std.log

import io.hamal.agent.extension.api.ExtensionFactory
import io.hamal.lib.kua.Extension
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput1Schema
import io.hamal.lib.kua.value.Function1In0Out
import io.hamal.lib.kua.value.NamedFunctionValue
import io.hamal.lib.kua.value.StringValue

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

class InfoFunction : Function1In0Out<StringValue>(
    FunctionInput1Schema(StringValue::class)
) {
    override fun invoke(ctx: FunctionContext, arg1: StringValue) {
        println("INFO: ${arg1.value}")
    }
}

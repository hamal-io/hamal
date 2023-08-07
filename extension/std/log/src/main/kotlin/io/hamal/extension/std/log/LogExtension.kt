package io.hamal.extension.std.log

import io.hamal.lib.domain.ExecLogLevel
import io.hamal.lib.domain.ExecLogMessage
import io.hamal.lib.domain.LocalAt
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.kua.extension.ScriptExtension
import io.hamal.lib.kua.extension.ScriptExtensionFactory
import io.hamal.lib.kua.function.Function2In1Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput2Schema
import io.hamal.lib.kua.function.FunctionOutput1Schema
import io.hamal.lib.kua.value.ErrorValue
import io.hamal.lib.kua.value.StringValue
import io.hamal.lib.sdk.HttpTemplateSupplier
import io.hamal.lib.sdk.domain.AppendExecLogCmd
import io.hamal.lib.sdk.service.DefaultExecLogService

class LogExtensionFactory(
    private val templateSupplier: HttpTemplateSupplier
) : ScriptExtensionFactory {
    override fun create(): ScriptExtension {
        return ScriptExtension(
            name = "log",
            internals = mapOf(
                "log" to LogFunction(templateSupplier),
            )
        )
    }
}

class LogFunction(
    private val templateSupplier: HttpTemplateSupplier
) : Function2In1Out<StringValue, StringValue, ErrorValue>(
    FunctionInput2Schema(StringValue::class, StringValue::class),
    FunctionOutput1Schema(ErrorValue::class)
) {

    override fun invoke(ctx: FunctionContext, arg1: StringValue, arg2: StringValue): ErrorValue? {
        val level = ExecLogLevel.valueOf(arg1.value)
        val message = ExecLogMessage(arg2.value)

        DefaultExecLogService(templateSupplier())
            .append(
                ExecId(1), AppendExecLogCmd(
                    level = level,
                    message = message,
                    localAt = LocalAt.now()
                )
            )

        return null
    }
}

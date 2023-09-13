package io.hamal.extension.std.log

import io.hamal.lib.domain._enum.ExecLogLevel
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.ExecLogMessage
import io.hamal.lib.domain.vo.LocalAt
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extension.ScriptExtension
import io.hamal.lib.kua.extension.ScriptExtensionFactory
import io.hamal.lib.kua.function.Function2In1Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput2Schema
import io.hamal.lib.kua.function.FunctionOutput1Schema
import io.hamal.lib.kua.type.ErrorType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.sdk.hub.HubAppendExecLogReq
import io.hamal.lib.sdk.hub.HubExecLogService
import logger


val log = logger(LogFunction::class)

class LogExtensionFactory(
    private val execLogService: HubExecLogService
) : ScriptExtensionFactory {
    override fun create(sandbox: Sandbox): ScriptExtension {
        return ScriptExtension(
            name = "log",
            internals = mapOf(
                "log" to LogFunction(execLogService),
            )
        )
    }
}

class LogFunction(
    private val execLogService: HubExecLogService
) : Function2In1Out<StringType, StringType, ErrorType>(
    FunctionInput2Schema(StringType::class, StringType::class),
    FunctionOutput1Schema(ErrorType::class)
) {

    override fun invoke(ctx: FunctionContext, arg1: StringType, arg2: StringType): ErrorType? {
        val level = ExecLogLevel.valueOf(arg1.value)
        val message = ExecLogMessage(arg2.value)

        if (level == ExecLogLevel.Info) {
            log.info(message.value)
        }

        execLogService.append(
            ctx[ExecId::class],
            HubAppendExecLogReq(
                level = level,
                message = message,
                localAt = LocalAt.now()
            )
        )

        return null
    }
}

package io.hamal.extension.std.log

import io.hamal.lib.common.logger
import io.hamal.lib.domain._enum.ExecLogLevel
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.ExecLogMessage
import io.hamal.lib.domain.vo.LocalAt
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.capability.Capability
import io.hamal.lib.kua.capability.CapabilityFactory
import io.hamal.lib.kua.function.Function2In1Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput2Schema
import io.hamal.lib.kua.function.FunctionOutput1Schema
import io.hamal.lib.kua.type.ErrorType
import io.hamal.lib.kua.type.StringType
import io.hamal.lib.sdk.api.ApiAppendExecLogCmd
import io.hamal.lib.sdk.api.ApiExecLogService


val log = logger(LogFunction::class)

class LogExtensionFactory(
    private val execLogService: ApiExecLogService
) : CapabilityFactory {
    override fun create(sandbox: Sandbox): Capability {
        return Capability(
            name = "log",
            internals = mapOf(
                "log" to LogFunction(execLogService),
            )
        )
    }
}

class LogFunction(
    private val execLogService: ApiExecLogService
) : Function2In1Out<StringType, StringType, ErrorType>(
    FunctionInput2Schema(StringType::class, StringType::class),
    FunctionOutput1Schema(ErrorType::class)
) {

    override fun invoke(ctx: FunctionContext, arg1: StringType, arg2: StringType): ErrorType? {
        val level = ExecLogLevel.valueOf(arg1.value)
        val message = ExecLogMessage(arg2.value)

        if (level == ExecLogLevel.Info) {
            log.info(message.value)
        } else if (level == ExecLogLevel.Warn) {
            log.warn(message.value)
        } else if (level == ExecLogLevel.Error) {
            log.error(message.value)
        }

        execLogService.append(
            ctx[ExecId::class],
            ApiAppendExecLogCmd(
                level = level,
                message = message,
                localAt = LocalAt.now()
            )
        )

        return null
    }
}

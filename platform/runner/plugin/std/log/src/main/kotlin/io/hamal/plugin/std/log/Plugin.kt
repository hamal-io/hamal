package io.hamal.plugin.std.log

import io.hamal.lib.common.logger
import io.hamal.lib.common.value.ValueError
import io.hamal.lib.common.value.ValueString
import io.hamal.lib.domain._enum.ExecLogLevels
import io.hamal.lib.domain._enum.ExecLogLevels.*

import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.ExecLogLevel.Companion.ExecLogLevel
import io.hamal.lib.domain.vo.ExecLogMessage.Companion.ExecLogMessage
import io.hamal.lib.domain.vo.ExecLogTimestamp
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extend.plugin.RunnerPlugin
import io.hamal.lib.kua.extend.plugin.RunnerPluginFactory
import io.hamal.lib.kua.function.Function2In1Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput2Schema
import io.hamal.lib.kua.function.FunctionOutput1Schema
import io.hamal.lib.sdk.api.ApiExecLogAppendRequest
import io.hamal.lib.sdk.api.ApiExecLogService


val log = logger(LogFunction::class)

class PluginLogFactory(
    private val execLogService: ApiExecLogService
) : RunnerPluginFactory {
    override fun create(sandbox: Sandbox): RunnerPlugin {
        return RunnerPlugin(
            name = ValueString("std.log"),
            internals = mapOf(
                ValueString("log") to LogFunction(execLogService),
            )
        )
    }
}

class LogFunction(
    private val execLogService: ApiExecLogService
) : Function2In1Out<ValueString, ValueString, ValueError>(
    FunctionInput2Schema(ValueString::class, ValueString::class),
    FunctionOutput1Schema(ValueError::class)
) {

    override fun invoke(ctx: FunctionContext, arg1: ValueString, arg2: ValueString): ValueError? {
        val level = ExecLogLevel(ExecLogLevels.valueOf(arg1.stringValue))
        val message = ExecLogMessage(arg2.stringValue)

        when (level.enumValue) {
            Trace -> log.trace(message.stringValue)
            Debug -> log.debug(message.stringValue)
            Info -> log.info(message.stringValue)
            Warn -> log.warn(message.stringValue)
            Error -> log.error(message.stringValue)
        }

        execLogService.append(
            ctx[ExecId::class],
            ApiExecLogAppendRequest(
                level = level,
                message = message,
                timestamp = ExecLogTimestamp.now()
            )
        )

        return null
    }
}

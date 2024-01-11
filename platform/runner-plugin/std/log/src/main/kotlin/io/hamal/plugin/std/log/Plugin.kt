package io.hamal.plugin.std.log

import io.hamal.lib.common.logger
import io.hamal.lib.domain._enum.ExecLogLevel
import io.hamal.lib.domain._enum.ExecLogLevel.*
import io.hamal.lib.domain.vo.ExecId
import io.hamal.lib.domain.vo.ExecLogMessage
import io.hamal.lib.domain.vo.ExecLogTimestamp
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extend.plugin.RunnerPlugin
import io.hamal.lib.kua.extend.plugin.RunnerPluginFactory
import io.hamal.lib.kua.function.Function2In1Out
import io.hamal.lib.kua.function.FunctionContext
import io.hamal.lib.kua.function.FunctionInput2Schema
import io.hamal.lib.kua.function.FunctionOutput1Schema
import io.hamal.lib.kua.type.KuaError
import io.hamal.lib.kua.type.KuaString
import io.hamal.lib.sdk.api.ApiExecLogAppendRequest
import io.hamal.lib.sdk.api.ApiExecLogService


val log = logger(LogFunction::class)

class PluginLogFactory(
    private val execLogService: ApiExecLogService
) : RunnerPluginFactory {
    override fun create(sandbox: Sandbox): RunnerPlugin {
        return RunnerPlugin(
            name = "log",
            internals = mapOf(
                "log" to LogFunction(execLogService),
            )
        )
    }
}

class LogFunction(
    private val execLogService: ApiExecLogService
) : Function2In1Out<KuaString, KuaString, KuaError>(
    FunctionInput2Schema(KuaString::class, KuaString::class),
    FunctionOutput1Schema(KuaError::class)
) {

    override fun invoke(ctx: FunctionContext, arg1: KuaString, arg2: KuaString): KuaError? {
        val level = ExecLogLevel.valueOf(arg1.value)
        val message = ExecLogMessage(arg2.value)

        when (level) {
            Trace -> log.trace(message.value)
            Debug -> log.debug(message.value)
            Info -> log.info(message.value)
            Warn -> log.warn(message.value)
            Error -> log.error(message.value)
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

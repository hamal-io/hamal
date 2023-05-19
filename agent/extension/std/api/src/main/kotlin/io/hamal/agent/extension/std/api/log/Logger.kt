package io.hamal.agent.extension.std.api.log

import io.hamal.lib.script.api.value.FunctionValue
import io.hamal.lib.script.api.value.Identifier
import io.hamal.lib.script.api.value.TableValue

interface Logger {
    val trace: FunctionValue
}

class LoggerTable(
    trace: FunctionValue
) : TableValue(
    entries = mapOf(
        Identifier("trace") to trace
    )
)
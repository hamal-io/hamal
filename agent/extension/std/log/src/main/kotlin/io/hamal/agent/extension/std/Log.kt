package io.hamal.agent.extension.std

import io.hamal.lib.script.api.Context

interface Log {
    fun invoke(level: LogLevel, ctx: Context)
}

enum class LogLevel {
    Trace,
    Debug,
    Info
}
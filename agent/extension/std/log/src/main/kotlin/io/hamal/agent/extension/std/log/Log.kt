package io.hamal.agent.extension.std.log


interface Log {
    fun invoke(level: LogLevel)
}

enum class LogLevel {
    Trace,
    Debug,
    Info
}
package io.hamal.lib.domain._enum

enum class ExecLogLevels(val value: Int) {
    Trace(1),
    Debug(2),
    Info(3),
    Warn(4),
    Error(5);

    companion object {
        @JvmStatic
        fun of(value: Int): ExecLogLevels {
            val result = ExecLogLevels.values().find { it.value == value }
            require(result != null) { "$value not mapped as a ExecLogLevel type" }
            return result
        }
    }
}
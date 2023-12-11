package io.hamal.lib.domain._enum

enum class ExecLogLevel(val value: Int) {
    Trace(0),
    Debug(1),
    Info(2),
    Warn(3),
    Error(4);

    companion object {
        @JvmStatic
        fun of(value: Int): ExecLogLevel {
            val result = ExecLogLevel.values().find { it.value == value }
            require(result != null) { "$value not mapped as a code type" }
            return result
        }
    }
}
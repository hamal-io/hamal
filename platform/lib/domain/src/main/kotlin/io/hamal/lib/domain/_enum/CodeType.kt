package io.hamal.lib.domain._enum

enum class CodeType(val value: Int) {
    None(0),
    Lua54(1);

    companion object {
        @JvmStatic
        fun of(value: Int): CodeType {
            val result = CodeType.values().find { it.value == value }
            require(result != null) { "$value not mapped as a code type" }
            return result
        }
    }
}
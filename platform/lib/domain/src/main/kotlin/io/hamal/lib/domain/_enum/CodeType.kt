package io.hamal.lib.domain._enum

enum class CodeTypes(val value: Int) {
    None(0),
    Lua54(1),
    Nodes(2);

    companion object {
        @JvmStatic
        fun of(value: Int): CodeTypes {
            val result = entries.find { it.value == value }
            require(result != null) { "$value not mapped as a code type" }
            return result
        }
    }
}
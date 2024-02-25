package io.hamal.lib.kua.type

sealed interface KuaType {
    enum class Type {
        Any,
        Boolean,
        Code,
        Decimal,
        Error,
        Function,
        Table,
        Nil,
        Number,
        String
    }

    val type: Type
}


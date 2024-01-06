package io.hamal.lib.kua.type

import io.hamal.lib.kua.type.KuaType.Type

sealed interface KuaType {
    enum class Type {
        Any,
        Array,
        Boolean,
        Code,
        Decimal,
        Error,
        Function,
        Map,
        Nil,
        Number,
        String
    }

    val type: Type
}

interface KuaTableType : KuaType

data class KuaAny(val value: KuaType) : KuaType {
    override val type: Type = Type.Any
}
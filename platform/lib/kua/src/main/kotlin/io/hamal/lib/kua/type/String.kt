package io.hamal.lib.kua.type

import io.hamal.lib.kua.type.KuaType.Type


data class KuaString(val value: String) : KuaType {
    override val type: Type = Type.String
    override fun toString(): String = value
}
package io.hamal.lib.kua.type

data class KuaAny(val value: KuaType) : KuaType {
    override val type: KuaType.Type = KuaType.Type.Any
}
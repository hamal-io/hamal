package io.hamal.lib.kua.type

data class KuaError(val value: String) : KuaType {
    override val type: KuaType.Type = KuaType.Type.Error
}
package io.hamal.lib.kua.type


@JvmInline
value class KuaReference(val value: Int) : KuaType {
    override val type: KuaType.Type get() = KuaType.Type.Reference
}
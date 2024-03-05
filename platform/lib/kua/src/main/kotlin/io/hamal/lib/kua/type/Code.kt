package io.hamal.lib.kua.type

import io.hamal.lib.kua.type.KuaType.Type.Code

@JvmInline
value class KuaCode(private val value: String) : KuaType {
    override val type: KuaType.Type get() = Code

    val stringValue: String get() = value
}
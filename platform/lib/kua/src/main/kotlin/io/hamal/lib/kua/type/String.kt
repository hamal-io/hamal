package io.hamal.lib.kua.type

import io.hamal.lib.kua.type.KuaType.Type


@JvmInline
value class KuaString(private val value: String) : KuaType {
    override val type: Type get() = Type.String
    override fun toString(): String = value

    val stringValue: String get() = value
}

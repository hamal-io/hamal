package io.hamal.lib.kua.type

import io.hamal.lib.kua.type.KuaType.Type

fun booleanOf(value: Boolean) = if (value) KuaTrue else KuaFalse

sealed class KuaBoolean(
    val value: Boolean,
) : KuaType {
    override val type: Type = Type.Boolean

}

object KuaTrue : KuaBoolean(true) {
    override fun toString() = "true"
}

object KuaFalse : KuaBoolean(false) {
    override fun toString() = "false"
}
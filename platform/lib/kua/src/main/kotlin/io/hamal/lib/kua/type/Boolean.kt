package io.hamal.lib.kua.type

import io.hamal.lib.kua.type.KuaType.Type

fun booleanOf(value: Boolean) = if (value) KuaTrue else KuaFalse

sealed class KuaBoolean(
    val value: Boolean,
) : KuaType {
    override val type: Type = Type.Boolean

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as KuaBoolean
        if (value != other.value) return false
        if (type != other.type) return false
        return true
    }

    override fun hashCode(): Int {
        var result = value.hashCode()
        result = 31 * result + type.hashCode()
        return result
    }

    companion object {
        fun of(value: Boolean) = if (value) KuaTrue else KuaFalse
    }
}

object KuaTrue : KuaBoolean(true) {
    override fun toString() = "true"
}

object KuaFalse : KuaBoolean(false) {
    override fun toString() = "false"
}
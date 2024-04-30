package io.hamal.lib.kua.type

fun booleanOf(value: Boolean) = if (value) KuaTrue else KuaFalse

sealed class KuaBoolean(
    private val value: Boolean,
) : KuaType {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as KuaBoolean
        if (value != other.value) return false
        return true
    }

    override fun hashCode(): Int {
        val result = value.hashCode()
        return result
    }

    companion object {
        fun of(value: Boolean) = if (value) KuaTrue else KuaFalse
    }

    val booleanValue: Boolean get() = value
}

object KuaTrue : KuaBoolean(true) {
    override fun toString() = "true"
}

object KuaFalse : KuaBoolean(false) {
    override fun toString() = "false"
}
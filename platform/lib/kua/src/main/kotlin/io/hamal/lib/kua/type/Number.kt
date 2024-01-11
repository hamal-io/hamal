package io.hamal.lib.kua.type

import io.hamal.lib.kua.type.KuaType.Type.Number

data class KuaNumber(val value: Double) : KuaType {
    constructor(value: Int) : this(value.toDouble())

    companion object {
        val Zero = KuaNumber(0.0)
        val One = KuaNumber(1.0)
    }

    operator fun times(value: Int) = KuaNumber(this.value * value)
    operator fun times(value: Double) = KuaNumber(this.value * value)

    override val type: KuaType.Type = Number

    override fun toString(): String = value.toString()
}
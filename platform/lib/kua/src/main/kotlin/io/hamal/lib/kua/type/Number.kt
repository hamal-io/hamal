package io.hamal.lib.kua.type

import io.hamal.lib.kua.type.KuaType.Type.Number

@JvmInline
value class KuaNumber(private val value: Double) : KuaType {
    constructor(value: Int) : this(value.toDouble())

    companion object {
        val Zero = KuaNumber(0.0)
        val One = KuaNumber(1.0)
    }

    operator fun times(value: Int) = KuaNumber(this.value * value)
    operator fun times(value: Double) = KuaNumber(this.value * value)

    override val type: KuaType.Type get() = Number

    override fun toString(): String = value.toString()

    val shortValue: Short get() = value.toInt().toShort()
    val intValue: Int get() = value.toInt()
    val longValue: Long get() = value.toLong()
    val floatValue: Float get() = value.toFloat()
    val doubleValue: Double get() = value.toDouble()
}
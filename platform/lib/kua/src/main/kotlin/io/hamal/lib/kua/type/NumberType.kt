package io.hamal.lib.kua.type

data class NumberType(val value: Double) : SerializableType() {
    constructor(value: Int) : this(value.toDouble())

    companion object {
        val Zero = NumberType(0.0)
        val One = NumberType(1.0)
    }

    operator fun times(value: Int) = NumberType(this.value * value)
    operator fun times(value: Double) = NumberType(this.value * value)

    override fun toString(): String {
        return "NumberType(${value.toBigDecimal().toPlainString()})"
    }
}
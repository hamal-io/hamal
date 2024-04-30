package io.hamal.lib.value

data object TypeNumber : Type() {
    override val identifier = TypeIdentifier("Number")
}

data object TypeListNumber : TypeList() {
    override val identifier = TypeIdentifier("List_Number")
    override val valueType = TypeNumber
}

@JvmInline
value class ValueNumber(private val value: Double) : Value {
    override val type get() = TypeNumber

    constructor(value: Int) : this(value.toDouble())

    companion object {
        val Zero = ValueNumber(0.0)
        val One = ValueNumber(1.0)
    }

    operator fun times(value: Int) = ValueNumber(this.value * value)
    operator fun times(value: Double) = ValueNumber(this.value * value)

    override fun toString(): String = value.toString()

    val shortValue: Short get() = value.toInt().toShort()
    val intValue: Int get() = value.toInt()
    val longValue: Long get() = value.toLong()
    val floatValue: Float get() = value.toFloat()
    val doubleValue: Double get() = value.toDouble()
}
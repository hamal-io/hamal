package io.hamal.lib.common.value

import io.hamal.lib.common.value.TypeIdentifier.Companion.TypeIdentifier
import java.math.BigInteger

data object TypeNumber : TypePrimitive() {
    override val identifier = TypeIdentifier("Number")
}

@JvmInline
value class ValueNumber(private val value: Double) : ValueComparable<ValueNumber> {
    override val type get() = TypeNumber

    constructor(value: Byte) : this(value.toDouble())
    constructor(value: Short) : this(value.toDouble())
    constructor(value: Int) : this(value.toDouble())
    constructor(value: Long) : this(value.toDouble())
    constructor(value: Float) : this(value.toDouble())
    constructor(value: Number) : this(value.toDouble())
    constructor(value: BigInteger) : this(value.toDouble())
    constructor(value: String) : this(value.toDouble())

    operator fun times(value: Int) = ValueNumber(this.value * value)
    operator fun times(value: Double) = ValueNumber(this.value * value)
    override fun compareTo(other: ValueNumber) = value.compareTo(other.value)

    override fun toString(): String = value.toString()

    val shortValue: Short get() = value.toInt().toShort()
    val intValue: Int get() = value.toInt()
    val longValue: Long get() = value.toLong()
    val floatValue: Float get() = value.toFloat()
    val doubleValue: Double get() = value
    val stringValue: String get() = value.toString()
}

abstract class ValueVariableNumber : ValueVariable.ComparableImpl<ValueNumber>() {
    val shortValue: Short get() = value.shortValue
    val intValue: Int get() = value.intValue
    val longValue: Long get() = value.longValue
    val floatValue: Float get() = value.floatValue
    val doubleValue: Double get() = value.doubleValue
}

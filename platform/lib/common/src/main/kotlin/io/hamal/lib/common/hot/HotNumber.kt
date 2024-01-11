package io.hamal.lib.common.hot

import java.math.BigDecimal
import java.math.BigInteger

class HotNumber(val value: BigDecimal) : HotTerminal {
    constructor(value: Byte) : this(value.toInt().toBigDecimal())
    constructor(value: Short) : this(value.toInt().toBigDecimal())
    constructor(value: Int) : this(value.toBigDecimal())
    constructor(value: Long) : this(value.toBigDecimal())
    constructor(value: Float) : this(value.toBigDecimal())
    constructor(value: Double) : this(value.toBigDecimal())
    constructor(value: Number) : this(BigDecimal.valueOf(value.toDouble()))
    constructor(value: BigInteger) : this(value.toBigDecimal())

    override val isNumber get() : Boolean = true

    override fun asNumber(): HotNumber = this

    override val bigDecimalValue get() : BigDecimal = value
    override val bigIntegerValue get() : BigInteger = value.toBigIntegerExact()
    override val byteValue get() : Byte = value.toByte()
    override val doubleValue get() : Double = value.toDouble()
    override val floatValue get() : Float = value.toFloat()
    override val intValue get() : Int = value.toInt()
    override val longValue get() : Long = value.toLong()
    override val shortValue get() : Short = value.toShort()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as HotNumber
        return value.compareTo(other.value) == 0
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }
}
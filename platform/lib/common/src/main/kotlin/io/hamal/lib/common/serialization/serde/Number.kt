package io.hamal.lib.common.serialization.serde

import io.hamal.lib.common.Decimal
import java.math.BigInteger


@JvmInline
value class SerdeNumber(val value: Decimal) : SerdePrimitive<SerdeNumber> {
    constructor(value: Byte) : this(value.toInt().toBigDecimal())
    constructor(value: Short) : this(value.toInt().toBigDecimal())
    constructor(value: Int) : this(value.toBigDecimal())
    constructor(value: Long) : this(value.toBigDecimal())
    constructor(value: Float) : this(value.toBigDecimal())
    constructor(value: Double) : this(value.toBigDecimal())
    constructor(value: kotlin.Number) : this(Decimal(value))
    constructor(value: BigInteger) : this(value.toBigDecimal())

    override val isNumber get() : Boolean = true

    override fun asNumber(): SerdeNumber = this

    override val decimalValue get() : Decimal = value
    override val byteValue get() : Byte = value.toByte()
    override val doubleValue get() : Double = value.toDouble()
    override val floatValue get() : Float = value.toFloat()
    override val intValue get() : Int = value.toInt()
    override val longValue get() : Long = value.toLong()
    override val shortValue get() : Short = value.toShort()

    override fun deepCopy() = this
}
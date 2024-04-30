package io.hamal.lib.value

import io.hamal.lib.common.Decimal
import io.hamal.lib.common.snowflake.SnowflakeId
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

data object TypeDecimal : Type() {
    override val identifier = TypeIdentifier("Decimal")
}

data object TypeListDecimal : TypeList() {
    override val identifier = TypeIdentifier("List_Decimal")
    override val valueType = TypeDecimal
}

@JvmInline
value class ValueDecimal(
    val value: Decimal,
) : Value, Comparable<ValueDecimal> {

    override val type get() = TypeDecimal

    companion object {
        val Zero = ValueDecimal(0)
        val One = ValueDecimal(1)

        operator fun invoke(value: Byte): ValueDecimal = ValueDecimal(Decimal(value))
        operator fun invoke(value: Short): ValueDecimal = ValueDecimal(Decimal(value))
        operator fun invoke(value: Int): ValueDecimal = ValueDecimal(Decimal(value))
        operator fun invoke(value: Long): ValueDecimal = ValueDecimal(Decimal(value))
        operator fun invoke(value: Number): ValueDecimal = ValueDecimal(Decimal(value))
        operator fun invoke(value: SnowflakeId): ValueDecimal = ValueDecimal(Decimal(value.value))

        operator fun invoke(value: Float): ValueDecimal {
            require(!value.isNaN()) { IllegalArgumentException("NaN") }
            require(!value.isInfinite()) { IllegalArgumentException("Infinity") }
            return ValueDecimal(BigDecimal.valueOf(value.toDouble()))
        }

        operator fun invoke(value: Double): ValueDecimal {
            require(!value.isNaN()) { IllegalArgumentException("NaN") }
            require(!value.isInfinite()) { IllegalArgumentException("Infinity") }
            return ValueDecimal(BigDecimal.valueOf(value))
        }

        operator fun invoke(value: String): ValueDecimal {
            require(isNumber(value)) { IllegalArgumentException("NaN") }
            return ValueDecimal(BigDecimal(value.trim(), mathContext))
        }

        fun isNumber(value: String): Boolean {
            val regex = "-?[0-9]+(\\.[0-9]+)?".toRegex()
            return value.matches(regex)
        }

        private val mathContext = MathContext.DECIMAL128
    }

    fun plus(other: ValueDecimal) = ValueDecimal(delegate.add(other.delegate, mathContext))

    fun minus(other: ValueDecimal) =
        ValueDecimal(delegate.subtract(other.delegate, mathContext))

    fun multiply(other: ValueDecimal) =
        ValueDecimal(delegate.multiply(other.delegate, mathContext))

    fun divide(other: ValueDecimal) =
        ValueDecimal(delegate.divide(other.delegate, mathContext))

    fun pow(other: ValueDecimal) =
        ValueDecimal(delegate.pow(other.delegate.toInt(), mathContext))

    fun remainder(other: ValueDecimal) =
        ValueDecimal(delegate.remainder(other.delegate, mathContext))

    fun floor() = ValueDecimal(delegate.setScale(0, RoundingMode.FLOOR))

    fun ceil() = ValueDecimal(delegate.setScale(0, RoundingMode.CEILING))

    fun ln(): ValueDecimal {
        // Algorithm: http://functions.wolfram.com/ElementaryFunctions/Log/10/
        // https://stackoverjob.com/a/6169691/6444586
        val result: ValueDecimal
        require(isPositive()) { IllegalStateException("Value must >= 1") }
        if (delegate == BigDecimal.ONE) {
            return ValueDecimal(BigDecimal.ZERO)
        } else {
            val iterations = 25000L
            val x = delegate.subtract(BigDecimal.ONE)
            var ret = BigDecimal(iterations + 1)
            for (i in iterations downTo 0) {
                var N = BigDecimal(i / 2 + 1).pow(2)
                N = N.multiply(x, mathContext)
                ret = N.divide(ret, mathContext)
                N = BigDecimal(i + 1)
                ret = ret.add(N, mathContext)
            }
            ret = x.divide(ret, mathContext)
            result = ValueDecimal(ret)
        }
        return result
    }

    fun sqrt(): ValueDecimal {
        require(!isNegative()) { throw IllegalStateException("Value must >= 0") }
        return ValueDecimal(delegate.sqrt(mathContext))
    }

    fun abs() = ValueDecimal(delegate.abs(mathContext))

    fun negate() = ValueDecimal(delegate.negate(mathContext))

    override fun toString() = value.toString()

    override operator fun compareTo(other: ValueDecimal) = value.compareTo(other.value)

    fun isLessThan(other: ValueDecimal) = compareTo(other) < 0

    fun isLessThanEqual(other: ValueDecimal) = compareTo(other) <= 0

    fun isGreaterThan(other: ValueDecimal) = compareTo(other) > 0

    fun isGreaterThanEqual(other: ValueDecimal) = compareTo(other) >= 0

    fun isNegative() = delegate.signum() < 0

    fun isPositive() = delegate.signum() > 0

    fun isZero() = delegate.signum() == 0

    fun toByte() = value.toByte()

    fun toShort() = value.toShort()

    fun toInt() = value.toInt()

    fun toLong() = value.toLong()

    fun toFloat() = value.toFloat()

    fun toChar() = value.toChar()

    fun toDouble() = value.toDouble()

    fun toBigDecimal() = value

    private val delegate get() = value.value
}
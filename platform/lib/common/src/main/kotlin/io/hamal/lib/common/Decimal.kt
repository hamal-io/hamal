package io.hamal.lib.common

import io.hamal.lib.common.snowflake.SnowflakeId
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode


class Decimal(val value: BigDecimal) : Comparable<Decimal> {


    companion object {
        val Zero = Decimal(0)
        val One = Decimal(1)

        operator fun invoke(value: Byte): Decimal = Decimal(BigDecimal.valueOf(value.toLong()))
        operator fun invoke(value: Short): Decimal = Decimal(BigDecimal.valueOf(value.toLong()))
        operator fun invoke(value: Int): Decimal = Decimal(BigDecimal.valueOf(value.toLong()))
        operator fun invoke(value: Long): Decimal = Decimal(BigDecimal.valueOf(value))
        operator fun invoke(value: Number): Decimal = Decimal(BigDecimal.valueOf(value.toDouble()))
        operator fun invoke(value: SnowflakeId): Decimal = Decimal(BigDecimal.valueOf(value.value))

        operator fun invoke(value: Float): Decimal {
            require(!value.isNaN()) { IllegalArgumentException("NaN") }
            require(!value.isInfinite()) { IllegalArgumentException("Infinity") }
            return Decimal(BigDecimal.valueOf(value.toDouble()))
        }

        operator fun invoke(value: Double): Decimal {
            require(!value.isNaN()) { IllegalArgumentException("NaN") }
            require(!value.isInfinite()) { IllegalArgumentException("Infinity") }
            return Decimal(BigDecimal.valueOf(value))
        }

        operator fun invoke(value: String): Decimal {
            require(isNumber(value)) { IllegalArgumentException("NaN") }
            return Decimal(BigDecimal(value.trim(), mathContext))
        }

        fun isNumber(value: String): Boolean {
            val regex = "-?[0-9]+(\\.[0-9]+)?".toRegex()
            return value.matches(regex)
        }

        private val mathContext = MathContext.DECIMAL128
    }

    fun plus(other: Decimal) = Decimal(value.add(other.value, mathContext))

    fun minus(other: Decimal) =
        Decimal(value.subtract(other.value, mathContext))

    fun multiply(other: Decimal) =
        Decimal(value.multiply(other.value, mathContext))

    fun divide(other: Decimal) =
        Decimal(value.divide(other.value, mathContext))

    fun pow(other: Decimal) =
        Decimal(value.pow(other.value.toInt(), mathContext))

    fun remainder(other: Decimal) =
        Decimal(value.remainder(other.value, mathContext))

    fun floor() = Decimal(value.setScale(0, RoundingMode.FLOOR))

    fun ceil() = Decimal(value.setScale(0, RoundingMode.CEILING))

    fun ln(): Decimal {
        // Algorithm: http://functions.wolfram.com/ElementaryFunctions/Log/10/
        // https://stackoverjob.com/a/6169691/6444586
        val result: Decimal
        require(isPositive()) { IllegalStateException("Value must >= 1") }
        if (value == BigDecimal.ONE) {
            return Decimal(BigDecimal.ZERO)
        } else {
            val iterations = 25000L
            val x = value.subtract(BigDecimal.ONE)
            var ret = BigDecimal(iterations + 1)
            for (i in iterations downTo 0) {
                var N = BigDecimal(i / 2 + 1).pow(2)
                N = N.multiply(x, mathContext)
                ret = N.divide(ret, mathContext)
                N = BigDecimal(i + 1)
                ret = ret.add(N, mathContext)
            }
            ret = x.divide(ret, mathContext)
            result = Decimal(ret)
        }
        return result
    }

    fun sqrt(): Decimal {
        require(!isNegative()) { throw IllegalStateException("Value must >= 0") }
        return Decimal(value.sqrt(mathContext))
    }

    fun abs() = Decimal(value.abs(mathContext))

    fun negate() = Decimal(value.negate(mathContext))


    override fun toString() = value.toString()

    override operator fun compareTo(other: Decimal) = value.compareTo(other.value)

    fun isLessThan(other: Decimal) = compareTo(other) < 0

    fun isLessThanEqual(other: Decimal) = compareTo(other) <= 0

    fun isGreaterThan(other: Decimal) = compareTo(other) > 0

    fun isGreaterThanEqual(other: Decimal) = compareTo(other) >= 0

    fun isNegative() = value.signum() < 0

    fun isPositive() = value.signum() > 0

    fun isZero() = value.signum() == 0

    fun toByte() = value.toByte()

    fun toShort() = value.toShort()

    fun toInt() = value.toInt()

    fun toLong() = value.toLong()

    fun toFloat() = value.toFloat()

    fun toChar() = value.toInt().toChar()

    fun toDouble() = value.toDouble()

    fun toBigDecimal() = value

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Decimal
        return !this.isLessThan(other) && !this.isGreaterThan(other)
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }

}
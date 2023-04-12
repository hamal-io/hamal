package io.hamal.lib.meta.math

import io.hamal.lib.meta.exception.IllegalArgumentException
import io.hamal.lib.meta.exception.IllegalStateException
import io.hamal.lib.meta.exception.throwIf
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode


data class Decimal constructor(
    private val delegate: BigDecimal
) : Number(), Comparable<Decimal> {

    companion object {
        val ZERO = Decimal(0)
        val ONE = Decimal(1)
        val mathContext = MathContext.DECIMAL64

        operator fun invoke(value: Byte): Decimal = Decimal(BigDecimal.valueOf(value.toLong()))
        operator fun invoke(value: Short): Decimal = Decimal(BigDecimal.valueOf(value.toLong()))
        operator fun invoke(value: Int): Decimal = Decimal(BigDecimal.valueOf(value.toLong()))
        operator fun invoke(value: Long): Decimal = Decimal(BigDecimal.valueOf(value))

        operator fun invoke(value: Float): Decimal {
            throwIf(value.isNaN()) { IllegalArgumentException("NaN") }
            throwIf(value.isInfinite()) { IllegalArgumentException("Infinity") }
            return Decimal(BigDecimal.valueOf(value.toDouble()))
        }

        operator fun invoke(value: Double): Decimal {
            throwIf(value.isNaN()) { IllegalArgumentException("NaN") }
            throwIf(value.isInfinite()) { IllegalArgumentException("Infinity") }
            return Decimal(BigDecimal.valueOf(value))
        }

        operator fun invoke(value: String): Decimal {
            throwIf(!value.isNumber()) { IllegalArgumentException("NaN") }
            return Decimal(BigDecimal(value.trim(), mathContext))
        }

        private fun String.isNumber(): Boolean {
            val regex = "-?[0-9]+(\\.[0-9]+)?".toRegex()
            return matches(regex)
        }
    }


    fun plus(other: Decimal) = Decimal(delegate.add(other.delegate, mathContext))

    fun minus(other: Decimal) = Decimal(delegate.subtract(other.delegate, mathContext))

    fun multiply(other: Decimal) = Decimal(delegate.multiply(other.delegate, mathContext))

    fun divide(other: Decimal) = Decimal(delegate.divide(other.delegate, mathContext))

    fun remainder(other: Decimal) = Decimal(delegate.remainder(other.delegate, mathContext))

    fun floor() = Decimal(delegate.setScale(0, RoundingMode.FLOOR))

    fun ceil() = Decimal(delegate.setScale(0, RoundingMode.CEILING))

    fun ln(): Decimal {
        // Algorithm: http://functions.wolfram.com/ElementaryFunctions/Log/10/
        // https://stackoverflow.com/a/6169691/6444586
        val result: Decimal
        throwIf(!isPositive()) { IllegalStateException("Value must >= 1") }
        if (delegate == BigDecimal.ONE) {
            return Decimal(0)
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
            result = Decimal(ret)
        }
        return result
    }

    fun sqrt(): Decimal {
        throwIf(isNegative()) { throw IllegalStateException("Value must >= 0") }
        return Decimal(delegate.sqrt(mathContext))
    }

    fun abs() = Decimal(delegate.abs(mathContext))

    fun negate() = Decimal(delegate.negate(mathContext))

    override fun toString() = delegate.toString()

    override operator fun compareTo(other: Decimal) = delegate.compareTo(other.delegate)

    fun isLessThan(other: Decimal) = compareTo(other) < 0

    fun isLessThanEqual(other: Decimal) = compareTo(other) <= 0

    fun isGreaterThan(other: Decimal) = compareTo(other) > 0

    fun isGreaterThanEqual(other: Decimal) = compareTo(other) >= 0

    fun isNegative() = delegate.signum() < 0

    fun isPositive() = delegate.signum() > 0

    fun isZero() = delegate.signum() == 0

    override fun toByte() = delegate.toByte()

    override fun toShort() = delegate.toShort()

    override fun toInt() = delegate.toInt()

    override fun toLong() = delegate.toLong()

    override fun toFloat() = delegate.toFloat()

    override fun toChar() = delegate.toChar()

    override fun toDouble() = delegate.toDouble()

    fun toBigDecimal() = delegate

}


package io.hamal.lib.kua.type

import io.hamal.lib.common.Decimal
import io.hamal.lib.common.snowflake.SnowflakeId
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode


@JvmInline
value class KuaDecimal(
    val value: Decimal,
) : KuaType, Comparable<KuaDecimal> {

    private val delegate get() = value.value

    companion object {
        val Zero = KuaDecimal(0)
        val One = KuaDecimal(1)

        operator fun invoke(value: Byte): KuaDecimal = KuaDecimal(Decimal(value))
        operator fun invoke(value: Short): KuaDecimal = KuaDecimal(Decimal(value))
        operator fun invoke(value: Int): KuaDecimal = KuaDecimal(Decimal(value))
        operator fun invoke(value: Long): KuaDecimal = KuaDecimal(Decimal(value))
        operator fun invoke(value: Number): KuaDecimal = KuaDecimal(Decimal(value))
        operator fun invoke(value: SnowflakeId): KuaDecimal = KuaDecimal(Decimal(value.value))

        operator fun invoke(value: Float): KuaDecimal {
            require(!value.isNaN()) { IllegalArgumentException("NaN") }
            require(!value.isInfinite()) { IllegalArgumentException("Infinity") }
            return KuaDecimal(BigDecimal.valueOf(value.toDouble()))
        }

        operator fun invoke(value: Double): KuaDecimal {
            require(!value.isNaN()) { IllegalArgumentException("NaN") }
            require(!value.isInfinite()) { IllegalArgumentException("Infinity") }
            return KuaDecimal(BigDecimal.valueOf(value))
        }

        operator fun invoke(value: String): KuaDecimal {
            require(isNumber(value)) { IllegalArgumentException("NaN") }
            return KuaDecimal(BigDecimal(value.trim(), mathContext))
        }

        fun isNumber(value: String): Boolean {
            val regex = "-?[0-9]+(\\.[0-9]+)?".toRegex()
            return value.matches(regex)
        }

        private val mathContext = MathContext.DECIMAL128
    }

    fun plus(other: KuaDecimal) = KuaDecimal(delegate.add(other.delegate, mathContext))

    fun minus(other: KuaDecimal) =
        KuaDecimal(delegate.subtract(other.delegate, mathContext))

    fun multiply(other: KuaDecimal) =
        KuaDecimal(delegate.multiply(other.delegate, mathContext))

    fun divide(other: KuaDecimal) =
        KuaDecimal(delegate.divide(other.delegate, mathContext))

    fun pow(other: KuaDecimal) =
        KuaDecimal(delegate.pow(other.delegate.toInt(), mathContext))

    fun remainder(other: KuaDecimal) =
        KuaDecimal(delegate.remainder(other.delegate, mathContext))

    fun floor() = KuaDecimal(delegate.setScale(0, RoundingMode.FLOOR))

    fun ceil() = KuaDecimal(delegate.setScale(0, RoundingMode.CEILING))

    fun ln(): KuaDecimal {
        // Algorithm: http://functions.wolfram.com/ElementaryFunctions/Log/10/
        // https://stackoverjob.com/a/6169691/6444586
        val result: KuaDecimal
        require(isPositive()) { IllegalStateException("Value must >= 1") }
        if (delegate == BigDecimal.ONE) {
            return KuaDecimal(BigDecimal.ZERO)
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
            result = KuaDecimal(ret)
        }
        return result
    }

    fun sqrt(): KuaDecimal {
        require(!isNegative()) { throw IllegalStateException("Value must >= 0") }
        return KuaDecimal(delegate.sqrt(mathContext))
    }

    fun abs() = KuaDecimal(delegate.abs(mathContext))

    fun negate() = KuaDecimal(delegate.negate(mathContext))


    override fun toString() = value.toString()

    override operator fun compareTo(other: KuaDecimal) = value.compareTo(other.value)

    fun isLessThan(other: KuaDecimal) = compareTo(other) < 0

    fun isLessThanEqual(other: KuaDecimal) = compareTo(other) <= 0

    fun isGreaterThan(other: KuaDecimal) = compareTo(other) > 0

    fun isGreaterThanEqual(other: KuaDecimal) = compareTo(other) >= 0

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
}
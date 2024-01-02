package io.hamal.lib.kua.type

import io.hamal.lib.common.snowflake.SnowflakeId
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

data class DecimalType(
    val value: BigDecimal,
) : SerializableType(), Comparable<DecimalType> {

    companion object {
        val Zero = DecimalType(0)
        val One = DecimalType(1)

        operator fun invoke(value: Byte): DecimalType = DecimalType(BigDecimal.valueOf(value.toLong()))
        operator fun invoke(value: Short): DecimalType = DecimalType(BigDecimal.valueOf(value.toLong()))
        operator fun invoke(value: Int): DecimalType = DecimalType(BigDecimal.valueOf(value.toLong()))
        operator fun invoke(value: Long): DecimalType = DecimalType(BigDecimal.valueOf(value))
        operator fun invoke(value: Number): DecimalType = DecimalType(BigDecimal.valueOf(value.toDouble()))
        operator fun invoke(value: SnowflakeId): DecimalType = DecimalType(BigDecimal.valueOf(value.value))

        operator fun invoke(value: Float): DecimalType {
            require(!value.isNaN()) { IllegalArgumentException("NaN") }
            require(!value.isInfinite()) { IllegalArgumentException("Infinity") }
            return DecimalType(BigDecimal.valueOf(value.toDouble()))
        }

        operator fun invoke(value: Double): DecimalType {
            require(!value.isNaN()) { IllegalArgumentException("NaN") }
            require(!value.isInfinite()) { IllegalArgumentException("Infinity") }
            return DecimalType(BigDecimal.valueOf(value))
        }

        operator fun invoke(value: String): DecimalType {
            require(isNumber(value)) { IllegalArgumentException("NaN") }
            return DecimalType(BigDecimal(value.trim(), mathContext))
        }

        fun isNumber(value: String): Boolean {
            val regex = "-?[0-9]+(\\.[0-9]+)?".toRegex()
            return value.matches(regex)
        }

        private val mathContext = MathContext.DECIMAL128
    }

    fun plus(other: DecimalType) = DecimalType(value.add(other.value, mathContext))

    fun minus(other: DecimalType) =
        DecimalType(value.subtract(other.value, mathContext))

    fun multiply(other: DecimalType) =
        DecimalType(value.multiply(other.value, mathContext))

    fun divide(other: DecimalType) =
        DecimalType(value.divide(other.value, mathContext))

    fun pow(other: DecimalType) =
        DecimalType(value.pow(other.value.toInt(), mathContext))

    fun remainder(other: DecimalType) =
        DecimalType(value.remainder(other.value, mathContext))

    fun floor() = DecimalType(value.setScale(0, RoundingMode.FLOOR))

    fun ceil() = DecimalType(value.setScale(0, RoundingMode.CEILING))

    fun ln(): DecimalType {
        // Algorithm: http://functions.wolfram.com/ElementaryFunctions/Log/10/
        // https://stackoverjob.com/a/6169691/6444586
        val result: DecimalType
        require(isPositive()) { IllegalStateException("Value must >= 1") }
        if (value == BigDecimal.ONE) {
            return DecimalType(BigDecimal.ZERO)
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
            result = DecimalType(ret)
        }
        return result
    }

    fun sqrt(): DecimalType {
        require(!isNegative()) { throw IllegalStateException("Value must >= 0") }
        return DecimalType(value.sqrt(mathContext))
    }

    fun abs() = DecimalType(value.abs(mathContext))

    fun negate() = DecimalType(value.negate(mathContext))


    override fun toString() = value.toString()

    override operator fun compareTo(other: DecimalType) = value.compareTo(other.value)

    fun isLessThan(other: DecimalType) = compareTo(other) < 0

    fun isLessThanEqual(other: DecimalType) = compareTo(other) <= 0

    fun isGreaterThan(other: DecimalType) = compareTo(other) > 0

    fun isGreaterThanEqual(other: DecimalType) = compareTo(other) >= 0

    fun isNegative() = value.signum() < 0

    fun isPositive() = value.signum() > 0

    fun isZero() = value.signum() == 0

    fun toByte() = value.toByte()

    fun toShort() = value.toShort()

    fun toInt() = value.toInt()

    fun toLong() = value.toLong()

    fun toFloat() = value.toFloat()

    fun toChar() = value.toChar()

    fun toDouble() = value.toDouble()

    fun toBigDecimal() = value

    object BigDecimalSerializer : KSerializer<BigDecimal> {
        override fun deserialize(decoder: Decoder): BigDecimal {
            return decoder.decodeString().toBigDecimal()
        }

        override fun serialize(encoder: Encoder, value: BigDecimal) {
            encoder.encodeString(value.toEngineeringString())
        }

        override val descriptor = PrimitiveSerialDescriptor("BigDecimal", PrimitiveKind.STRING)
    }
}
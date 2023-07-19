package io.hamal.lib.kua.value

import io.hamal.lib.common.SnowflakeId
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

@Serializable
@SerialName("DecimalValue")
data class DecimalValue(
    @Serializable(with = BigDecimalSerializer::class)
    val value: BigDecimal
) : Number(), Value, Comparable<DecimalValue> {
    companion object {
        val Zero = DecimalValue(0)
        val One = DecimalValue(1)
        val mathContext = MathContext.DECIMAL128

        operator fun invoke(value: Byte): DecimalValue = DecimalValue(BigDecimal.valueOf(value.toLong()))
        operator fun invoke(value: Short): DecimalValue = DecimalValue(BigDecimal.valueOf(value.toLong()))
        operator fun invoke(value: Int): DecimalValue = DecimalValue(BigDecimal.valueOf(value.toLong()))
        operator fun invoke(value: Long): DecimalValue = DecimalValue(BigDecimal.valueOf(value))
        operator fun invoke(value: Number): DecimalValue = DecimalValue(BigDecimal.valueOf(value.toDouble()))
        operator fun invoke(value: SnowflakeId): DecimalValue = DecimalValue(BigDecimal.valueOf(value.value))

        operator fun invoke(value: Float): DecimalValue {
            require(!value.isNaN()) { IllegalArgumentException("NaN") }
            require(!value.isInfinite()) { IllegalArgumentException("Infinity") }
            return DecimalValue(BigDecimal.valueOf(value.toDouble()))
        }

        operator fun invoke(value: Double): DecimalValue {
            require(!value.isNaN()) { IllegalArgumentException("NaN") }
            require(!value.isInfinite()) { IllegalArgumentException("Infinity") }
            return DecimalValue(BigDecimal.valueOf(value))
        }

        operator fun invoke(value: String): DecimalValue {
            require(value.isNumber()) { IllegalArgumentException("NaN") }
            return DecimalValue(BigDecimal(value.trim(), mathContext))
        }

        private fun String.isNumber(): Boolean {
            val regex = "-?[0-9]+(\\.[0-9]+)?".toRegex()
            return matches(regex)
        }
    }

    fun plus(other: DecimalValue) =
        DecimalValue(value.add(other.value, mathContext))

    fun minus(other: DecimalValue) =
        DecimalValue(value.subtract(other.value, mathContext))

    fun multiply(other: DecimalValue) =
        DecimalValue(value.multiply(other.value, mathContext))

    fun divide(other: DecimalValue) =
        DecimalValue(value.divide(other.value, mathContext))

    fun pow(other: DecimalValue) =
        DecimalValue(value.pow(other.value.toInt(), mathContext))

    fun remainder(other: DecimalValue) =
        DecimalValue(value.remainder(other.value, mathContext))

    fun floor() = DecimalValue(value.setScale(0, RoundingMode.FLOOR))

    fun ceil() = DecimalValue(value.setScale(0, RoundingMode.CEILING))

    fun ln(): DecimalValue {
        // Algorithm: http://functions.wolfram.com/ElementaryFunctions/Log/10/
        // https://stackoverjob.com/a/6169691/6444586
        val result: DecimalValue
        require(isPositive()) { IllegalStateException("Value must >= 1") }
        if (value == BigDecimal.ONE) {
            return DecimalValue(BigDecimal.ZERO)
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
            result = DecimalValue(ret)
        }
        return result
    }

    fun sqrt(): DecimalValue {
        require(!isNegative()) { throw IllegalStateException("Value must >= 0") }
        return DecimalValue(value.sqrt(mathContext))
    }

    fun abs() = DecimalValue(value.abs(mathContext))

    fun negate() = DecimalValue(value.negate(mathContext))


    override fun toString() = value.toString()

    override operator fun compareTo(other: DecimalValue) = value.compareTo(other.value)

    fun isLessThan(other: DecimalValue) = compareTo(other) < 0

    fun isLessThanEqual(other: DecimalValue) = compareTo(other) <= 0

    fun isGreaterThan(other: DecimalValue) = compareTo(other) > 0

    fun isGreaterThanEqual(other: DecimalValue) = compareTo(other) >= 0

    fun isNegative() = value.signum() < 0

    fun isPositive() = value.signum() > 0

    fun isZero() = value.signum() == 0

    override fun toByte() = value.toByte()

    override fun toShort() = value.toShort()

    override fun toInt() = value.toInt()

    override fun toLong() = value.toLong()

    override fun toFloat() = value.toFloat()

    override fun toChar() = value.toChar()

    override fun toDouble() = value.toDouble()

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

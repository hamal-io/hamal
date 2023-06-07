package io.hamal.lib.common.value

import io.hamal.lib.common.value.ValueOperation.Type.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

@Serializable
@SerialName("NumberValue")
data class NumberValue(
    @Serializable(with = BigDecimalSerializer::class)
    val value: BigDecimal,
    @Transient
    override val metaTable: MetaTable = DefaultNumberMetaTable
) : Number(), Value, Comparable<NumberValue> {
    companion object {
        val Zero = NumberValue(0)
        val One = NumberValue(1)
        val mathContext = MathContext.DECIMAL128

        operator fun invoke(value: Byte): NumberValue = NumberValue(BigDecimal.valueOf(value.toLong()))
        operator fun invoke(value: Short): NumberValue = NumberValue(BigDecimal.valueOf(value.toLong()))
        operator fun invoke(value: Int): NumberValue = NumberValue(BigDecimal.valueOf(value.toLong()))
        operator fun invoke(value: Long): NumberValue = NumberValue(BigDecimal.valueOf(value))

        operator fun invoke(value: Float): NumberValue {
            require(!value.isNaN()) { IllegalArgumentException("NaN") }
            require(!value.isInfinite()) { IllegalArgumentException("Infinity") }
            return NumberValue(BigDecimal.valueOf(value.toDouble()))
        }

        operator fun invoke(value: Double): NumberValue {
            require(!value.isNaN()) { IllegalArgumentException("NaN") }
            require(!value.isInfinite()) { IllegalArgumentException("Infinity") }
            return NumberValue(BigDecimal.valueOf(value))
        }

        operator fun invoke(value: String): NumberValue {
            require(value.isNumber()) { IllegalArgumentException("NaN") }
            return NumberValue(BigDecimal(value.trim(), mathContext))
        }

        private fun String.isNumber(): Boolean {
            val regex = "-?[0-9]+(\\.[0-9]+)?".toRegex()
            return matches(regex)
        }
    }

    fun plus(other: NumberValue) =
        NumberValue(value.add(other.value, mathContext), metaTable)

    fun minus(other: NumberValue) =
        NumberValue(value.subtract(other.value, mathContext), metaTable)

    fun multiply(other: NumberValue) =
        NumberValue(value.multiply(other.value, mathContext), metaTable)

    fun divide(other: NumberValue) =
        NumberValue(value.divide(other.value, mathContext), metaTable)

    fun pow(other: NumberValue) =
        NumberValue(value.pow(other.value.toInt(), mathContext), metaTable)

    fun remainder(other: NumberValue) =
        NumberValue(value.remainder(other.value, mathContext), metaTable)

    fun floor() = NumberValue(value.setScale(0, RoundingMode.FLOOR), metaTable)

    fun ceil() = NumberValue(value.setScale(0, RoundingMode.CEILING), metaTable)

    fun ln(): NumberValue {
        // Algorithm: http://functions.wolfram.com/ElementaryFunctions/Log/10/
        // https://stackoverjob.com/a/6169691/6444586
        val result: NumberValue
        require(isPositive()) { IllegalStateException("Value must >= 1") }
        if (value == BigDecimal.ONE) {
            return NumberValue(BigDecimal.ZERO, metaTable)
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
            result = NumberValue(ret, metaTable)
        }
        return result
    }

    fun sqrt(): NumberValue {
        require(!isNegative()) { throw IllegalStateException("Value must >= 0") }
        return NumberValue(value.sqrt(mathContext), metaTable)
    }

    fun abs() = NumberValue(value.abs(mathContext), metaTable)

    fun negate() = NumberValue(value.negate(mathContext), metaTable)


    override fun toString() = value.toString()

    override operator fun compareTo(other: NumberValue) = value.compareTo(other.value)

    fun isLessThan(other: NumberValue) = compareTo(other) < 0

    fun isLessThanEqual(other: NumberValue) = compareTo(other) <= 0

    fun isGreaterThan(other: NumberValue) = compareTo(other) > 0

    fun isGreaterThanEqual(other: NumberValue) = compareTo(other) >= 0

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


object DefaultNumberMetaTable : MetaTable {
    override val type = "number"

    override val operations = listOf(
        numberInfix(Add) { self, other -> self.plus(other) },
        numberInfix(Div) { self, other -> self.divide(other) },
        numberInfix(Eq) { self, other -> booleanOf(self == other) },
        numberInfix(Gt) { self, other -> booleanOf(self.isGreaterThan(other)) },
        numberInfix(Gte) { self, other -> booleanOf(self.isGreaterThanEqual(other)) },
        numberInfix(Lt) { self, other -> booleanOf(self.isLessThan(other)) },
        numberInfix(Lte) { self, other -> booleanOf(self.isLessThanEqual(other)) },
        numberInfix(Mod) { self, other -> self.remainder(other) },
        numberInfix(Mul) { self, other -> self.multiply(other) },
        numberPrefix(Negate) { self -> self.negate() },
        numberInfix(Pow) { self, other -> self.pow(other) },
        numberInfix(Sub) { self, other -> self.minus(other) },


        object : InfixValueOperation {
            override val selfType = type
            override val otherType = "nil"
            override val operationType = Eq
            override fun invoke(self: Value, other: Value) = FalseValue
        },

        object : InfixValueOperation {
            override val selfType = type
            override val otherType = "nil"
            override val operationType = Neq
            override fun invoke(self: Value, other: Value) = TrueValue
        })
}

private fun numberInfix(
    operation: ValueOperation.Type,
    fn: (self: NumberValue, other: NumberValue) -> Value
): InfixValueOperation {
    return object : InfixValueOperation {
        override val operationType = operation
        override val selfType = "number"
        override val otherType = "number"
        override operator fun invoke(self: Value, other: Value): Value {
            require(self is NumberValue)
            require(other is NumberValue)
            return fn(self, other)
        }
    }
}

private fun numberPrefix(
    operation: ValueOperation.Type,
    fn: (self: NumberValue) -> Value
): PrefixValueOperation {
    return object : PrefixValueOperation {
        override val operationType = operation
        override val selfType = "number"
        override operator fun invoke(self: Value): Value {
            require(self is NumberValue)
            return fn(self)
        }
    }
}

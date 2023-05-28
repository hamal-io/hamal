package io.hamal.lib.script.api.value

import io.hamal.lib.script.api.value.ValueOperation.Type.*
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode


data class DepNumberValue(
    private val delegate: BigDecimal,
    override val metaTable: DepMetaTable = DefaultNumberMetaTable
) : Number(), DepValue, Comparable<DepNumberValue> {

    companion object {
        val Zero = DepNumberValue(0)
        val One = DepNumberValue(1)
        val mathContext = MathContext.DECIMAL128

        operator fun invoke(value: Byte): DepNumberValue = DepNumberValue(BigDecimal.valueOf(value.toLong()))
        operator fun invoke(value: Short): DepNumberValue = DepNumberValue(BigDecimal.valueOf(value.toLong()))
        operator fun invoke(value: Int): DepNumberValue = DepNumberValue(BigDecimal.valueOf(value.toLong()))
        operator fun invoke(value: Long): DepNumberValue = DepNumberValue(BigDecimal.valueOf(value))

        operator fun invoke(value: Float): DepNumberValue {
            require(!value.isNaN()) { IllegalArgumentException("NaN") }
            require(!value.isInfinite()) { IllegalArgumentException("Infinity") }
            return DepNumberValue(BigDecimal.valueOf(value.toDouble()))
        }

        operator fun invoke(value: Double): DepNumberValue {
            require(!value.isNaN()) { IllegalArgumentException("NaN") }
            require(!value.isInfinite()) { IllegalArgumentException("Infinity") }
            return DepNumberValue(BigDecimal.valueOf(value))
        }

        operator fun invoke(value: String): DepNumberValue {
            require(value.isNumber()) { IllegalArgumentException("NaN") }
            return DepNumberValue(BigDecimal(value.trim(), mathContext))
        }

        private fun String.isNumber(): Boolean {
            val regex = "-?[0-9]+(\\.[0-9]+)?".toRegex()
            return matches(regex)
        }
    }


    fun plus(other: DepNumberValue) =
        DepNumberValue(delegate.add(other.delegate, mathContext), metaTable)

    fun minus(other: DepNumberValue) =
        DepNumberValue(delegate.subtract(other.delegate, mathContext), metaTable)

    fun multiply(other: DepNumberValue) =
        DepNumberValue(delegate.multiply(other.delegate, mathContext), metaTable)

    fun divide(other: DepNumberValue) =
        DepNumberValue(delegate.divide(other.delegate, mathContext), metaTable)

    fun pow(other: DepNumberValue) =
        DepNumberValue(delegate.pow(other.delegate.toInt(), mathContext), metaTable)

    fun remainder(other: DepNumberValue) =
        DepNumberValue(delegate.remainder(other.delegate, mathContext), metaTable)

    fun floor() = DepNumberValue(delegate.setScale(0, RoundingMode.FLOOR), metaTable)

    fun ceil() = DepNumberValue(delegate.setScale(0, RoundingMode.CEILING), metaTable)

    fun ln(): DepNumberValue {
        // Algorithm: http://functions.wolfram.com/ElementaryFunctions/Log/10/
        // https://stackoverjob.com/a/6169691/6444586
        val result: DepNumberValue
        require(isPositive()) { IllegalStateException("Value must >= 1") }
        if (delegate == BigDecimal.ONE) {
            return DepNumberValue(BigDecimal.ZERO, metaTable)
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
            result = DepNumberValue(ret, metaTable)
        }
        return result
    }

    fun sqrt(): DepNumberValue {
        require(!isNegative()) { throw IllegalStateException("Value must >= 0") }
        return DepNumberValue(delegate.sqrt(mathContext), metaTable)
    }

    fun abs() = DepNumberValue(delegate.abs(mathContext), metaTable)

    fun negate() = DepNumberValue(delegate.negate(mathContext), metaTable)


    override fun toString() = delegate.toString()

    override operator fun compareTo(other: DepNumberValue) = delegate.compareTo(other.delegate)

    fun isLessThan(other: DepNumberValue) = compareTo(other) < 0

    fun isLessThanEqual(other: DepNumberValue) = compareTo(other) <= 0

    fun isGreaterThan(other: DepNumberValue) = compareTo(other) > 0

    fun isGreaterThanEqual(other: DepNumberValue) = compareTo(other) >= 0

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


object DefaultNumberMetaTable : DepMetaTable {
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
            override val selfType = "number"
            override val otherType = "nil"
            override val operationType = Eq
            override fun invoke(self: DepValue, other: DepValue) = DepFalseValue
        },

        object : InfixValueOperation {
            override val selfType = "number"
            override val otherType = "nil"
            override val operationType = Neq
            override fun invoke(self: DepValue, other: DepValue) = DepTrueValue
        },

        )
}

private fun numberInfix(
    operation: ValueOperation.Type,
    fn: (self: DepNumberValue, other: DepNumberValue) -> DepValue
): InfixValueOperation {
    return object : InfixValueOperation {
        override val operationType = operation
        override val selfType = "number"
        override val otherType = "number"
        override operator fun invoke(self: DepValue, other: DepValue): DepValue {
            require(self is DepNumberValue)
            require(other is DepNumberValue)
            return fn(self, other)
        }
    }
}

private fun numberPrefix(
    operation: ValueOperation.Type,
    fn: (self: DepNumberValue) -> DepValue
): PrefixValueOperation {
    return object : PrefixValueOperation {
        override val operationType = operation
        override val selfType = "number"
        override operator fun invoke(self: DepValue): DepValue {
            require(self is DepNumberValue)
            return fn(self)
        }
    }
}

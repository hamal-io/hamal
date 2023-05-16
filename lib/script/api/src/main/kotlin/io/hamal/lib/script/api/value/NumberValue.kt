package io.hamal.lib.script.api.value

import io.hamal.lib.common.math.Decimal
import io.hamal.lib.script.api.value.ValueOperation.Type.*
import java.math.BigInteger

data class NumberValue(
    val value: Decimal,
    override val metaTable: MetaTable = DefaultNumberMetaTable
) : Value {
    constructor(value: String) : this(Decimal(value), DefaultNumberMetaTable)
    constructor(value: Int) : this(Decimal(value), DefaultNumberMetaTable)
    constructor(value: BigInteger) : this(Decimal(value.toBigDecimal()), DefaultNumberMetaTable)

    override fun toString() = value.toString()
}

object DefaultNumberMetaTable : MetaTable {
    override val type = "number"
    override val operations = listOf(
        numberInfix(Add) { self, other -> NumberValue(self.value.plus(other.value), self.metaTable) },
        numberInfix(Div) { self, other -> NumberValue(self.value.divide(other.value), self.metaTable) },
        numberInfix(GT) { self, other -> booleanOf(self.value.isGreaterThan(other.value)) },
        numberInfix(GTE) { self, other -> booleanOf(self.value.isGreaterThanEqual(other.value)) },
        numberInfix(LT) { self, other -> booleanOf(self.value.isLessThan(other.value)) },
        numberInfix(LTE) { self, other -> booleanOf(self.value.isLessThanEqual(other.value)) },
        numberInfix(Mod) { self, other -> NumberValue(self.value.remainder(other.value), self.metaTable) },
        numberInfix(Mul) { self, other -> NumberValue(self.value.multiply(other.value), self.metaTable) },
        numberPrefix(Negate) { self -> NumberValue(self.value.negate(), self.metaTable) },
        numberInfix(Pow) { self, other -> NumberValue(self.value.pow(other.value), self.metaTable) },
        numberInfix(Sub) { self, other -> NumberValue(self.value.minus(other.value), self.metaTable) }
    )
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
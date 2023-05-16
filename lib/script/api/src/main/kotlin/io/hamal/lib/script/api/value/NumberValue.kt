package io.hamal.lib.script.api.value

import io.hamal.lib.common.math.Decimal
import io.hamal.lib.script.api.value.ValueOperation.Type.Add
import io.hamal.lib.script.api.value.ValueOperation.Type.Sub
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
    override val operations = listOf<ValueOperation>(
        NumberAddNumber,
        NumberSubNumber
    )
}


private object NumberAddNumber : InfixValueOperation {
    override val operationType = Add
    override val selfType = "number"
    override val otherType = "number"
    override operator fun invoke(self: Value, other: Value): Value {
        require(self is NumberValue)
        require(other is NumberValue)
        return NumberValue(self.value.plus(other.value), self.metaTable)
    }

}

private object NumberSubNumber : InfixValueOperation {
    override val operationType = Sub
    override val selfType = "number"
    override val otherType = "number"
    override operator fun invoke(self: Value, other: Value): Value {
        require(self is NumberValue)
        require(other is NumberValue)
        return NumberValue(self.value.minus(other.value), self.metaTable)
    }

}
package io.hamal.lib.script.api.value

import io.hamal.lib.script.api.value.ValueOperation.Type.Eq
import io.hamal.lib.script.api.value.ValueOperation.Type.Neq

object DepNilValue : DepValue {
    override val metaTable = DepDefaultNilMetaTable
    override fun toString(): String {
        return "nil"
    }
}

object DepDefaultNilMetaTable : DepMetaTable {
    override val type = "nil"
    override val operations = listOf<ValueOperation>(
        object : InfixValueOperation {
            override val selfType = "nil"
            override val otherType = "nil"
            override val operationType = Eq
            override fun invoke(self: DepValue, other: DepValue) = DepTrueValue
        },
        object : InfixValueOperation {
            override val selfType = "nil"
            override val otherType = "nil"
            override val operationType = Neq
            override fun invoke(self: DepValue, other: DepValue) = DepFalseValue
        },

        object : InfixValueOperation {
            override val selfType = "nil"
            override val otherType = "number"
            override val operationType = Eq
            override fun invoke(self: DepValue, other: DepValue) = booleanOf(other is DepNilValue)
        },

        object : InfixValueOperation {
            override val selfType = "nil"
            override val otherType = "number"
            override val operationType = Neq
            override fun invoke(self: DepValue, other: DepValue) = booleanOf(other !is DepNilValue)
        },
    )
}

fun nilInfix(
    operation: ValueOperation.Type,
    otherType: String,
    fn: (other: DepValue) -> DepValue
) {

}
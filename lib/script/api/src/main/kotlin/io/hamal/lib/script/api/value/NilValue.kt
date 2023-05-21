package io.hamal.lib.script.api.value

import io.hamal.lib.script.api.value.ValueOperation.Type.Eq
import io.hamal.lib.script.api.value.ValueOperation.Type.Neq

object NilValue : Value {
    override val metaTable = DefaultNilMetaTable
    override fun toString(): String {
        return "nil"
    }
}

object DefaultNilMetaTable : MetaTable {
    override val type = "nil"
    override val operations = listOf<ValueOperation>(
        object : InfixValueOperation {
            override val selfType = "nil"
            override val otherType = "nil"
            override val operationType = Eq
            override fun invoke(self: Value, other: Value) = TrueValue
        },
        object : InfixValueOperation {
            override val selfType = "nil"
            override val otherType = "nil"
            override val operationType = Neq
            override fun invoke(self: Value, other: Value) = FalseValue
        },

        object : InfixValueOperation {
            override val selfType = "nil"
            override val otherType = "number"
            override val operationType = Eq
            override fun invoke(self: Value, other: Value) = booleanOf(other is NilValue)
        },

        object : InfixValueOperation {
            override val selfType = "nil"
            override val otherType = "number"
            override val operationType = Neq
            override fun invoke(self: Value, other: Value) = booleanOf(other !is NilValue)
        },
    )
}

fun nilInfix(
    operation: ValueOperation.Type,
    otherType: String,
    fn: (other: Value) -> Value
) {

}
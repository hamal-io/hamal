package io.hamal.lib.script.api.value

import io.hamal.lib.script.api.value.ValueOperator.Type.Eq
import io.hamal.lib.script.api.value.ValueOperator.Type.Neq
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("NilValue")
object NilValue : Value {
    @Transient
    override val metaTable = DefaultNilValueMetaTable
    override fun toString(): String {
        return "nil"
    }
}

object DefaultNilValueMetaTable : MetaTable<NilValue> {
    override val type = "nil"
    override val operators = listOf<ValueOperator>(
        nilInfix(Eq, "nil") { TrueValue },
        nilInfix(Neq, "nil") { FalseValue },

        nilInfix(Eq, "number") { booleanOf(it is NilValue) },
        nilInfix(Neq, "number") { booleanOf(it !is NilValue) },
    )
    override val props: Map<IdentValue, ValueProp<NilValue>> = mapOf()
}


fun nilInfix(
    operator: ValueOperator.Type,
    otherType: String,
    fn: (other: Value) -> Value
): InfixValueOperator {
    return object : InfixValueOperator {
        override val selfType = DefaultNilValueMetaTable.type
        override val otherType = otherType
        override val operatorType = operator
        override fun invoke(self: Value, other: Value) = fn(other)
    }
}
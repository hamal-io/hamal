package io.hamal.lib.script.api.value

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@SerialName("StringValue")
data class StringValue(val value: String) : Value {
    @Transient
    override val metaTable = DefaultStringValueMetaTable
    override fun toString(): String = value
}

object DefaultStringValueMetaTable : MetaTable {
    override val type = "string"
    override val operators: List<ValueOperator> = listOf(
        stringInfix(ValueOperator.Type.Eq) { self, other -> booleanOf(self == other) },
        stringInfix(ValueOperator.Type.Neq) { self, other -> booleanOf(self != other) },
    )
}

private fun stringInfix(
    operatorType: ValueOperator.Type,
    fn: (self: StringValue, other: StringValue) -> Value
): InfixValueOperator {
    return object : InfixValueOperator {
        override val operatorType = operatorType
        override val selfType = "string"
        override val otherType = "string"
        override operator fun invoke(self: Value, other: Value): Value {
            require(self is StringValue)
            require(other is StringValue)
            return fn(self, other)
        }
    }
}
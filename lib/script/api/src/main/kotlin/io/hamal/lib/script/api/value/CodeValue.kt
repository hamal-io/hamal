package io.hamal.lib.script.api.value

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@SerialName("CodeValue")
data class CodeValue(val value: String) : Value {
    constructor(str: StringValue) : this(str.value)

    @Transient
    override val metaTable = DefaultCodeValueMetaTable

}

object DefaultCodeValueMetaTable : MetaTable<CodeValue> {
    override val type = "code"
    override val operators: List<ValueOperator> = listOf(
        codeInfix(ValueOperator.Type.Eq) { self, other -> booleanOf(self == other) },
        codeInfix(ValueOperator.Type.Neq) { self, other -> booleanOf(self != other) },
    )
    override val props: Map<IdentValue, ValueProp<CodeValue>> = mapOf()
}


private fun codeInfix(
    operatorType: ValueOperator.Type,
    fn: (self: CodeValue, other: CodeValue) -> Value
): InfixValueOperator {
    return object : InfixValueOperator {
        override val operatorType = operatorType
        override val selfType = "code"
        override val otherType = "code"
        override operator fun invoke(self: Value, other: Value): Value {
            require(self is CodeValue)
            require(other is CodeValue)
            return fn(self, other)
        }
    }
}
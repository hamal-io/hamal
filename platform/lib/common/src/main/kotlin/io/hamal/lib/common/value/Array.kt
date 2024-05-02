package io.hamal.lib.common.value

class TypeArray(
    val valueType: Type,
    override val identifier: TypeIdentifier
) : Type() {
    companion object {
        fun TypeArray(valueType: Type, identifier: String) = TypeArray(valueType, TypeIdentifier(ValueString(identifier)))
    }

    operator fun invoke(vararg any: Any): ValueArray {
        return ValueArray(this, any.map { v -> Property.mapTypeToValue(valueType, v) })
    }
}

data class ValueArray(
    override val type: Type,
    val value: List<Value>
) : Value {
    override fun toString() = value.toString()
}
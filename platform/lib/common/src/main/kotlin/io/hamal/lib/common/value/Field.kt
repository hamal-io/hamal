package io.hamal.lib.common.value


class FieldIdentifier(override val value: ValueString) : ValueVariableString()

data class Field(
    val type: Type,
    val identifier: FieldIdentifier,
    val valueType: TypeObject? = null
) {
    constructor(type: Type, identifier: String, valueType: TypeObject? = null) : this(
        type,
        FieldIdentifier(ValueString(identifier)),
        valueType
    )
}

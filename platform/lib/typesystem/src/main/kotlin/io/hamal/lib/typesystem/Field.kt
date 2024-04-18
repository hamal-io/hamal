package io.hamal.lib.typesystem

import io.hamal.lib.common.domain.ValueObjectString
import io.hamal.lib.typesystem.type.Type
import io.hamal.lib.typesystem.type.TypeObject

class FieldIdentifier(override val value: String) : ValueObjectString()

data class Field(
    val type: Type,
    val identifier: FieldIdentifier,
    val valueType: TypeObject? = null
) {
    constructor(type: Type, identifier: String, valueType: TypeObject? = null) : this(type, FieldIdentifier(identifier), valueType)
}

package io.hamal.lib.typesystem

import io.hamal.lib.typesystem.type.Type
import io.hamal.lib.typesystem.type.TypeObject

data class Field(
    val type: Type,
    val identifier: String,
    val valueType: TypeObject? = null
)

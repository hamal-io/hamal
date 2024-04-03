package io.hamal.lib.typesystem.value

import io.hamal.lib.typesystem.Field.Kind
import io.hamal.lib.typesystem.Type

data class ValueList(
    val value: List<Value>,
    val valueType: Type
) : Value {
    override val kind get() = Kind.List
    override fun toString() = value.toString()
}
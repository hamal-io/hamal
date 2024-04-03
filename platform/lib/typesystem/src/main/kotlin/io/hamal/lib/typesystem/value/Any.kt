package io.hamal.lib.typesystem.value

import io.hamal.lib.typesystem.Field

data class ValueAny(
    val value: Value
) : Value {
    override val kind get() = Field.Kind.Any
    val valueKind get() = value.kind
    override fun toString() = value.toString()
}
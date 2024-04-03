package io.hamal.lib.typesystem.value

import io.hamal.lib.typesystem.Field.Kind

data class ValueOneOf<T : Value>(
    val value: T
) : Value {
    override val kind get() = Kind.Any
    val valueKind get() = value.kind
    override fun toString() = value.toString()
}
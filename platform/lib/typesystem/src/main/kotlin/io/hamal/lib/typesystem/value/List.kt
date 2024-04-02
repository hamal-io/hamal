package io.hamal.lib.typesystem.value

import io.hamal.lib.typesystem.Kind

data class ValueList<T : Value>(
    val value: List<T>
) : Value {
    override val kind get() = Kind.List
    override fun toString() = value.toString()
}
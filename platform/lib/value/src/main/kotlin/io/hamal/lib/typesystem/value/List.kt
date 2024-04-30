package io.hamal.lib.typesystem.value

import io.hamal.lib.typesystem.type.Type

data class ValueList(
    override val type: Type,
    val value: List<Value>
) : Value {
    override fun toString() = value.toString()
}
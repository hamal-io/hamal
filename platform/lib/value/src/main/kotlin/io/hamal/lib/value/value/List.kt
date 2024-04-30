package io.hamal.lib.value.value

import io.hamal.lib.value.type.Type

data class ValueList(
    override val type: Type,
    val value: List<Value>
) : Value {
    override fun toString() = value.toString()
}
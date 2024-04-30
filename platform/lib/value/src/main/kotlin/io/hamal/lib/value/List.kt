package io.hamal.lib.value

data class ValueList(
    override val type: Type,
    val value: List<Value>
) : Value {
    override fun toString() = value.toString()
}
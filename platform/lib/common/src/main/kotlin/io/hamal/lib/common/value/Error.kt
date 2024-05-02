package io.hamal.lib.common.value

import io.hamal.lib.common.value.TypeIdentifier.Companion.TypeIdentifier

data object TypeError : TypePrimitive() {
    override val identifier = TypeIdentifier("Error")
}


@JvmInline
value class ValueError(private val value: String) : Value {
    override val type get() = TypeError
    override fun toString(): String = value
    val stringValue: String get() = value
}
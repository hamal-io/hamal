package io.hamal.lib.typesystem.value

import io.hamal.lib.typesystem.type.TypeString

@JvmInline
value class ValueString(private val value: String) : Value {
    override val type get() = TypeString
    override fun toString(): String = value

    val stringValue: String get() = value
}

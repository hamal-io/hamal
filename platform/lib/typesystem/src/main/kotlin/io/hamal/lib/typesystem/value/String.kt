package io.hamal.lib.typesystem.value

import io.hamal.lib.typesystem.Kind

@JvmInline
value class ValueString(private val value: String) : Value {
    override val kind get() = Kind.String
    override fun toString(): String = value

    val stringValue: String get() = value
}

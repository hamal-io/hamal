package io.hamal.lib.common.value

import io.hamal.lib.common.value.TypeIdentifier.Companion.TypeIdentifier

data object TypeString : Type() {
    override val identifier = TypeIdentifier("String")
}

@JvmInline
value class ValueString(private val value: String) : ValueComparable<ValueString> {
    override val type get() = TypeString
    override fun compareTo(other: ValueString) = value.compareTo(other.value)
    override fun toString(): String = value
    val stringValue: String get() = value
}

abstract class ValueVariableString : ValueVariable.ComparableImpl<ValueString>() {
    val stringValue: String get() = value.stringValue
}

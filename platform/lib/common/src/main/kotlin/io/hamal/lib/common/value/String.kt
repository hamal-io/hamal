package io.hamal.lib.common.value

val TypeString = ValueType("String")

@JvmInline
value class ValueString(private val value: String) : ValueSerializable, ValueComparable<ValueString> {
    override val type get() = TypeString
    override fun compareTo(other: ValueString) = value.compareTo(other.value)
    override fun toString(): String = value
    val stringValue: String get() = value
}

abstract class ValueVariableString : ValueVariable.ComparableImpl<ValueString>() {
    val stringValue: String get() = value.stringValue
}

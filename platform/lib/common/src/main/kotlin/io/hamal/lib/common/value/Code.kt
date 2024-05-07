package io.hamal.lib.common.value

val TypeCode = ValueType("Code")

@JvmInline
value class ValueCode(private val value: String) : ValueSerializable, ValueComparable<ValueCode> {
    override val type get() = TypeCode
    override fun compareTo(other: ValueCode) = value.compareTo(other.value)
    override fun toString(): String = value
    val stringValue: String get() = value
}

abstract class ValueVariableCode : ValueVariable.ComparableImpl<ValueCode>() {
    val stringValue: String get() = value.stringValue
}
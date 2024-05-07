package io.hamal.lib.common.value

import io.hamal.lib.common.util.InstantUtils
import java.time.Instant

val TypeInstant = ValueType("Instant")

@JvmInline
value class ValueInstant(private val value: Instant) : ValueSerializable, ValueComparable<ValueInstant> {
    override val type get() = TypeInstant
    override fun toString(): String = value.toString()
    override fun compareTo(other: ValueInstant) = value.compareTo(other.value)
    val instantValue: Instant get() = value
    val stringValue: String get() = InstantUtils.format(value)
}

abstract class ValueVariableInstant : ValueVariable.ComparableImpl<ValueInstant>() {
    val instantValue: Instant get() = value.instantValue
}
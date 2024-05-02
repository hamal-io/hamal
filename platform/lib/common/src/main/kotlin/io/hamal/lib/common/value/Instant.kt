package io.hamal.lib.common.value

import io.hamal.lib.common.value.TypeIdentifier.Companion.TypeIdentifier
import java.time.Instant

data object TypeInstant : TypePrimitive() {
    override val identifier = TypeIdentifier("Instant")
}

@JvmInline
value class ValueInstant(private val value: Instant) : ValueComparable<ValueInstant> {
    override val type get() = TypeInstant
    override fun toString(): String = value.toString()
    override fun compareTo(other: ValueInstant) = value.compareTo(other.value)
    val instantValue: Instant get() = value
}

abstract class ValueVariableInstant : ValueVariable.ComparableImpl<ValueInstant>() {
    val instantValue: Instant get() = value.instantValue
}
package io.hamal.lib.common.value

import io.hamal.lib.common.value.TypeIdentifier.Companion.TypeIdentifier
import java.time.LocalTime

data object TypeTime : TypePrimitive() {
    override val identifier = TypeIdentifier("Time")
}

@JvmInline
value class ValueTime(private val value: LocalTime) : Value {
    override val type get() = TypeTime
    override fun toString(): String = value.toString()

    val timeValue: LocalTime get() = value
}

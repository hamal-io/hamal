package io.hamal.lib.typesystem.value

import io.hamal.lib.typesystem.Kind
import java.time.LocalTime

@JvmInline
value class ValueTime(private val value: LocalTime) : Value {
    override val kind get() = Kind.Time
    override fun toString(): String = value.toString()

    val timeValue: LocalTime get() = value
}

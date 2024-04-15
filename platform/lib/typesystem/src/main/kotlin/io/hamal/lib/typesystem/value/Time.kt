package io.hamal.lib.typesystem.value

import io.hamal.lib.typesystem.type.TypeTime
import java.time.LocalTime

@JvmInline
value class ValueTime(private val value: LocalTime) : Value {
    override val type get() = TypeTime
    override fun toString(): String = value.toString()

    val timeValue: LocalTime get() = value
}

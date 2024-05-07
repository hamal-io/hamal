package io.hamal.lib.common.value

import java.time.LocalTime

val TypeTime = ValueType("Time")

@JvmInline
value class ValueTime(private val value: LocalTime) : ValueSerializable {
    override val type get() = TypeTime
    override fun toString(): String = value.toString()

    val timeValue: LocalTime get() = value
}

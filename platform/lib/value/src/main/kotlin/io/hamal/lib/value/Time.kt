package io.hamal.lib.value

import java.time.LocalTime

data object TypeTime : Type() {
    override val identifier = TypeIdentifier("Time")
}

data object TypeListTime : TypeList() {
    override val identifier = TypeIdentifier("List_Time")
    override val valueType = TypeNumber
}

@JvmInline
value class ValueTime(private val value: LocalTime) : Value {
    override val type get() = TypeTime
    override fun toString(): String = value.toString()

    val timeValue: LocalTime get() = value
}

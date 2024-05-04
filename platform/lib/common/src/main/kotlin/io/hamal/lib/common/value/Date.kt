package io.hamal.lib.common.value

import java.time.LocalDate

val TypeDate = ValueType("Date")

@JvmInline
value class ValueDate(private val value: LocalDate) : Value {
    override val type get() = TypeDate
    override fun toString(): String = value.toString()

    val dateValue: LocalDate get() = value
}

package io.hamal.lib.value.value

import io.hamal.lib.value.type.TypeDate
import java.time.LocalDate

@JvmInline
value class ValueDate(private val value: LocalDate) : Value {
    override val type get() = TypeDate
    override fun toString(): String = value.toString()

    val dateValue: LocalDate get() = value
}

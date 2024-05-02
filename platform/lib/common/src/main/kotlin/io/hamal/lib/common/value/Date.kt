package io.hamal.lib.common.value

import io.hamal.lib.common.value.TypeIdentifier.Companion.TypeIdentifier
import java.time.LocalDate

data object TypeDate : TypePrimitive() {
    override val identifier = TypeIdentifier("Date")
}

@JvmInline
value class ValueDate(private val value: LocalDate) : Value {
    override val type get() = TypeDate
    override fun toString(): String = value.toString()

    val dateValue: LocalDate get() = value
}

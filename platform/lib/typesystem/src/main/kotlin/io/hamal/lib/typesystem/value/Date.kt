package io.hamal.lib.typesystem.value

import io.hamal.lib.typesystem.Field.Kind
import java.time.LocalDate

@JvmInline
value class ValueDate(private val value: LocalDate) : Value {
    override val kind get() = Kind.Date
    override fun toString(): String = value.toString()

    val dateValue: LocalDate get() = value
}

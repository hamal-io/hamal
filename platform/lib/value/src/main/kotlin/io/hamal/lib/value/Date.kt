package io.hamal.lib.value

import java.time.LocalDate

data object TypeDate : Type() {
    override val identifier = TypeIdentifier("Date")
}

data object TypeListDate : TypeList() {
    override val identifier = TypeIdentifier("List_Date")
    override val valueType = TypeNumber
}

@JvmInline
value class ValueDate(private val value: LocalDate) : Value {
    override val type get() = TypeDate
    override fun toString(): String = value.toString()

    val dateValue: LocalDate get() = value
}

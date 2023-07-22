package io.hamal.lib.kua.table

import io.hamal.lib.kua.value.Value

@JvmInline
value class TableLength(val value: Int)

interface Table : Value {
    val index: Int

    fun length(): TableLength
}
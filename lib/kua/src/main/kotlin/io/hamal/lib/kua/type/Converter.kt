package io.hamal.lib.kua.type

import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.function.FunctionType
import io.hamal.lib.kua.table.TableTypeMap

fun Sandbox.convertTableMap(table: TableType): TableTypeMap =
    tableCreateMap(table.size).apply {
        table.forEach { entry ->
            when (val value = entry.value) {
                is StringType -> set(entry.key, value)
                is NumberType -> set(entry.key, value)
                is FunctionType<*, *, *, *> -> set(entry.key, value)
//                is TableTypeArray -> set(entry.key, value)
//                is TableTypeMap -> set(entry.key, value)
                else -> TODO()
            }
        }
    }

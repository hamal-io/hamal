package io.hamal.lib.kua.type

import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.State
import io.hamal.lib.kua.function.FunctionType
import io.hamal.lib.kua.table.TableEntryIterator
import io.hamal.lib.kua.table.TableProxyArray
import io.hamal.lib.kua.table.TableProxyMap

// FIXME State instead of Sandbox
fun Sandbox.toTableProxyMap(table: TableType): TableProxyMap =
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


fun State.toTableType(map: TableProxyMap): TableType {
    val store = mutableMapOf<StringType, SerializableType>()

    TableEntryIterator(
        map.index,
        this,
        keyExtractor = { state, index ->
            state.getStringType(index)
        },
        valueExtractor = { state, index ->
            state.getAny(index)
        }
    ).forEach { (key, value) ->
        when (value.value) {
            is StringType -> store[key] = value.value
            is NumberType -> store[key] = value.value
            else -> TODO()
        }
    }

    return TableType(store)
}

fun State.toArrayType(array: TableProxyArray): ArrayType {
    val result = ArrayType()
    TableEntryIterator(
        index = array.index,
        state = this,
        keyExtractor = { state, index -> state.getNumberType(index) },
        valueExtractor = { state, index ->
            when (val value = state.getAny(index).value) {
                is BooleanType,
                is NumberType,
                is StringType -> value as SerializableType

                is TableProxyMap -> toMapType(value)
                is TableProxyArray -> toArrayType(value)
                else -> TODO("$value")
            }
        }
    ).forEach { (_, value) -> result.append(value) }

    return result
}

internal fun State.toMapType(map: TableProxyMap): MapType {
    val store = mutableMapOf<String, SerializableType>()

    TableEntryIterator(
        index = map.index,
        state = this,
        keyExtractor = { state, index -> state.getStringType(index) },
        valueExtractor = { state, index ->
            when (val value = state.getAny(index).value) {
                is BooleanType,
                is NumberType,
                is StringType -> value as SerializableType

                is TableProxyMap -> toMapType(value)
                is TableProxyArray -> toArrayType(value)
                else -> TODO("$value")
            }
        }
    ).forEach { (key, value) -> store[key.value] = value }

    return MapType(store)
}


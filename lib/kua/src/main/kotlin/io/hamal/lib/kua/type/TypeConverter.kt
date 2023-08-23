package io.hamal.lib.kua.type

import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.State
import io.hamal.lib.kua.function.FunctionType
import io.hamal.lib.kua.table.TableEntryIterator
import io.hamal.lib.kua.table.TableProxyArray
import io.hamal.lib.kua.table.TableProxyMap

// FIXME State instead of Sandbox
@Deprecated("remove me")
fun Sandbox.toTableProxyMap(table: DepTableType): TableProxyMap =
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

@Deprecated("remove me")
fun State.toTableType(map: TableProxyMap): DepTableType {
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

    return DepTableType(store)
}

fun State.toArrayType(array: TableProxyArray): ArrayType {
    val result = ArrayType()
    TableEntryIterator(
        index = array.index,
        state = this,
        keyExtractor = { state, index -> state.getNumberType(index) },
        valueExtractor = { state, index ->
            when (val value = state.getAny(index).value) {
                is ArrayType,
                is BooleanType,
                is MapType,
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

internal fun State.toProxyArray(array: ArrayType): TableProxyArray {
    return tableCreateArray(array.size).also {
        // FIXME probably instead of of appending it should be set to keep the index
        array.entries.forEach { (_, value) ->
            when (value) {
                is BooleanType -> it.append(value)
                is NumberType -> it.append(value)
                is StringType -> it.append(value)
                is MapType -> it.append(toProxyMap(value))
                is ArrayType -> it.append(toProxyArray(value))
                else -> TODO("$value")
            }
        }
    }
}

internal fun State.toMapType(map: TableProxyMap): MapType {
    val store = mutableMapOf<String, SerializableType>()

    TableEntryIterator(
        index = map.index,
        state = this,
        keyExtractor = { state, index -> state.getStringType(index) },
        valueExtractor = { state, index ->
            when (val value = state.getAny(index).value) {
                is ArrayType,
                is BooleanType,
                is MapType,
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

internal fun State.toProxyMap(map: MapType): TableProxyMap {
    return tableCreateMap(map.size).also {
        map.entries.forEach { (key, value) ->
            when (value) {
                is BooleanType -> it[key] = value
                is NumberType -> it[key] = value
                is StringType -> it[key] = value
                is MapType -> it[key] = toProxyMap(value)
                is ArrayType -> it[key] = toProxyArray(value)
                else -> TODO("$value")
            }
        }
    }
}
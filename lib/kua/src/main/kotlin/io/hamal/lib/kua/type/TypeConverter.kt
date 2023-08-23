package io.hamal.lib.kua.type

import io.hamal.lib.kua.State
import io.hamal.lib.kua.table.TableEntryIterator
import io.hamal.lib.kua.table.TableProxyArray
import io.hamal.lib.kua.table.TableProxyMap

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

fun State.toProxyArray(array: ArrayType): TableProxyArray {
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

fun State.toMapType(map: TableProxyMap): MapType {
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

fun State.toProxyMap(map: MapType): TableProxyMap {
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
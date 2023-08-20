package io.hamal.lib.kua.type

import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.State
import io.hamal.lib.kua.function.FunctionType
import io.hamal.lib.kua.table.TableEntryIterator
import io.hamal.lib.kua.table.TableTypeArray
import io.hamal.lib.kua.table.TableTypeMap

// FIXME State instead of Sandbox
fun Sandbox.toTableProxyMap(table: TableType): TableTypeMap =
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


fun State.toTableType(map: TableTypeMap): TableType {
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

fun State.toArrayType(array: TableTypeArray): ArrayType {
//    val store = mutableMapOf<Int, SerializableType>()
    val result = ArrayType()

    TableEntryIterator(
        index = array.index,
        state = this,
        keyExtractor = { state, index -> state.getNumberType(index) },
        valueExtractor = { state, index ->
            val anyValue = state.getAny(index)
            when (anyValue.value) {
                is NumberType -> anyValue.value
                is StringType -> anyValue.value
                is TableTypeMap -> toMapType(anyValue.value)
                else -> TODO("${anyValue.value}")
            }
        }
    ).forEach { (key, value) ->
        println("$key $value")
        when (value) {
            is NumberType -> result.append(value)
            is StringType -> result.append(value)
            is MapType -> result.append(value)
            else -> TODO("${value} not implemented")
        }
    }

    return result
}

fun State.toMapType(map: TableTypeMap): MapType {
    val store = mutableMapOf<String, SerializableType>()

    TableEntryIterator(
        index = map.index,
        state = this,
        keyExtractor = { state, index -> state.getStringType(index) },
        valueExtractor = { state, index -> state.getAny(index) }
    ).forEach { (key, value) ->
        when (value.value) {
            is NumberType -> store[key.value] = value.value
            is StringType -> store[key.value] = value.value
            is TableTypeMap -> store[key.value] = MapType()
            else -> TODO("$value not implemented")
        }
    }

    return MapType(store)
}


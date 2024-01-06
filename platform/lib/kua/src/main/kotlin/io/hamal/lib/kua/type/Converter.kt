package io.hamal.lib.kua.type

import io.hamal.lib.kua.State
import io.hamal.lib.kua.table.TableEntryIterator
import io.hamal.lib.kua.table.TableProxyArray
import io.hamal.lib.kua.table.TableProxyMap

fun State.toArrayType(array: TableProxyArray): KuaArray {
    val result = KuaArray()
    TableEntryIterator(
        index = array.index,
        state = this,
        keyExtractor = { state, index -> state.getNumberType(index) },
        valueExtractor = { state, index ->
            when (val value = state.getAny(index).value) {
                is KuaArray,
                is KuaBoolean,
                is KuaDecimal,
                is KuaMap,
                is KuaNumber,
                is KuaString -> value as KuaType

                is TableProxyMap -> toMapType(value)
                is TableProxyArray -> toArrayType(value)
                else -> TODO("$value")
            }
        }
    ).forEach { (_, value) -> result.append(value) }

    return result
}

fun State.toProxyArray(array: KuaArray): TableProxyArray {
    return tableCreateArray(array.size).also {
        // FIXME probably instead of of appending it should be set to keep the index
        array.value.forEach { (_, value) ->
            when (value) {
                is KuaBoolean -> it.append(value)
                is KuaDecimal -> it.append(value)
                is KuaNumber -> it.append(value)
                is KuaString -> it.append(value)
                is KuaMap -> {
                    it.append(toProxyMap(value)); pop(1)
                }

                is KuaArray -> {
                    it.append(toProxyArray(value)); pop(1)
                }

                else -> TODO("$value")
            }
        }
    }
}

fun State.toMapType(map: TableProxyMap): KuaMap {
    val store = mutableMapOf<String, KuaType>()

    TableEntryIterator(
        index = map.index,
        state = this,
        keyExtractor = { state, index -> state.getStringType(index) },
        valueExtractor = { state, index ->
            when (val value = state.getAny(index).value) {
                is KuaArray,
                is KuaBoolean,
                is KuaDecimal,
                is KuaMap,
                is KuaNumber,
                is KuaString -> value as KuaType

                is TableProxyMap -> toMapType(value)
                is TableProxyArray -> toArrayType(value)
                else -> TODO("$value")
            }
        }
    ).forEach { (key, value) -> store[key.value] = value }

    return KuaMap(store)
}

fun State.toProxyMap(map: KuaMap): TableProxyMap {
    return tableCreateMap(map.size).also {
        map.value.forEach { (key, value) ->
            when (value) {
                is KuaBoolean -> it[key] = value
                is KuaDecimal -> it[key] = value
                is KuaCode -> it[key] = value
                is KuaNil -> it[key] = KuaNil
                is KuaNumber -> it[key] = value
                is KuaString -> it[key] = value
                is KuaMap -> {
                    it[key] = toProxyMap(value); pop(1)
                }

                is KuaArray -> {
                    it[key] = toProxyArray(value); pop(1)
                }

                else -> TODO("$value")
            }
        }
    }
}
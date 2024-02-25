package io.hamal.lib.kua.type

import io.hamal.lib.kua.State
import io.hamal.lib.kua.table.TableEntryIterator
import io.hamal.lib.kua.table.TableProxyArray
import io.hamal.lib.kua.table.TableProxyMap

fun State.toKuaTableArray(array: TableProxyArray): KuaTable.Array {
    val result = KuaTable.Array()
    TableEntryIterator(
        index = array.index,
        state = this,
        keyExtractor = { state, index -> state.getNumberType(index) },
        valueExtractor = { state, index ->
            when (val value = state.getAny(index).value) {
//                is KuaTable.Array,
                is KuaBoolean,
                is KuaDecimal,
//                is KuaTable.Map,
                is KuaNumber,
                is KuaString -> value

                is TableProxyMap -> toKuaTableMap(value)
                is TableProxyArray -> toKuaTableArray(value)
                else -> TODO("$value")
            }
        }
    ).forEach { (_, value) -> result.append(value) }

    return result
}

fun State.toTableProxyArray(array: KuaTable.Array): TableProxyArray {
    if (array is TableProxyArray) {
        return array
    }
    return tableCreateArray(array.size).also {
        // FIXME probably instead of of appending it should be set to keep the index
        array.underlyingArray.forEach { (_, value) ->
            when (value) {
                is KuaBoolean -> it.append(value)
                is KuaDecimal -> it.append(value)
                is KuaNumber -> it.append(value)
                is KuaString -> it.append(value)
                is KuaTable.Map -> {
                    it.append(toTableProxyMap(value)); pop(1)
                }

                is KuaTable.Array -> {
                    it.append(toTableProxyArray(value)); pop(1)
                }

                else -> TODO("$value")
            }
        }
    }
}

fun State.toKuaTableMap(map: TableProxyMap): KuaTable.Map {
    val store = mutableMapOf<String, KuaType>()

    TableEntryIterator(
        index = map.index,
        state = this,
        keyExtractor = { state, index -> state.getStringType(index) },
        valueExtractor = { state, index ->
            when (val value = state.getAny(index).value) {
                is KuaBoolean,
                is KuaDecimal,
//                is KuaTable,
                is KuaNumber,
                is KuaString -> value

                is TableProxyMap -> toKuaTableMap(value)
                is TableProxyArray -> toKuaTableArray(value)

                is KuaTable.Map -> value
                is KuaTable.Array -> value
                else -> TODO("$value")
            }
        }
    ).forEach { (key, value) -> store[key.value] = value }

    return KuaTable.Map(store)
}

fun State.toTableProxyMap(map: KuaTable.Map): TableProxyMap {
    if (map is TableProxyMap) {
        return map
    }

    return tableCreateMap(map.size).also {
        // FIXME this really sucks
        val list = map.entries().toList()
        println(list)

        map.entries().forEach { (key, value) ->
            when (value) {
                is KuaBoolean -> it[key] = value
                is KuaDecimal -> it[key.value] = value
                is KuaCode -> it[key] = value
                is KuaNil -> it[key] = KuaNil
                is KuaNumber -> it[key] = value
                is KuaString -> it[key] = value
                is KuaTable.Map -> {
                    it[key.value] = toTableProxyMap(value); pop(1)
                }

                is KuaTable.Array -> {
                    it[key.value] = toTableProxyArray(value); pop(1)
                }

                else -> TODO("$value")
            }
        }
    }
}
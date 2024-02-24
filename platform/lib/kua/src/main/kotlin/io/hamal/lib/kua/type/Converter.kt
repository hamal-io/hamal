package io.hamal.lib.kua.type

import io.hamal.lib.kua.State
import io.hamal.lib.kua.table.TableEntryIterator
import io.hamal.lib.kua.table.TableProxy

fun State.toKuaTable(map: TableProxy): KuaTable {
    val store = mutableMapOf<String, KuaType>()

    TableEntryIterator(
        index = map.index,
        state = this,
        keyExtractor = { state, index -> state.getStringType(index) },
        valueExtractor = { state, index ->
            when (val value = state.getAny(index).value) {
                is KuaBoolean,
                is KuaDecimal,
                is KuaTable,
                is KuaNumber,
                is KuaString -> value

                is TableProxy -> toKuaTable(value)
                else -> TODO("$value")
            }
        }
    ).forEach { (key, value) -> store[key.value] = value }

    return KuaTable(store)
}

fun State.toTableProxy(map: KuaTable): TableProxy {
    return tableCreate(map.size).also {
        map.value.forEach { (key, value) ->
            when (value) {
                is KuaBoolean -> it[key] = value
                is KuaDecimal -> it[key] = value
                is KuaCode -> it[key] = value
                is KuaNil -> it[key] = KuaNil
                is KuaNumber -> it[key] = value
                is KuaString -> it[key] = value
                is KuaTable -> {
                    it[key] = toTableProxy(value); pop(1)
                }

                else -> TODO("$value")
            }
        }
    }
}
package io.hamal.lib.kua.table

import io.hamal.lib.kua.Bridge
import io.hamal.lib.kua.value.StringValue

interface TableMap {
    operator fun set(key: StringValue, value: StringValue)
    operator fun set(key: String, value: StringValue): TableLength
    operator fun set(key: String, value: String)
}

class DefaultTableMap(
    val bridge: Bridge
) : TableMap {

    override fun set(key: StringValue, value: StringValue) {
        bridge.pushString(key.value)
        bridge.pushString(value.value)
        bridge.tableSetRaw(-2)
        bridge.pop(1)
    }

    override fun set(key: String, value: StringValue): TableLength {
        bridge.pushString(key)
        bridge.pushString(value.value)
        return TableLength(bridge.tableSetRaw(bridge.top() - 2))
    }

    override fun set(key: String, value: String) {
        bridge.pushString(key)
        bridge.pushString(value)
        bridge.tableSetRaw(-2)
        bridge.pop(1)
    }
}


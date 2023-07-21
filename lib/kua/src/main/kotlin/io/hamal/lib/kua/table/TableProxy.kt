package io.hamal.lib.kua.table

import io.hamal.lib.kua.Bridge
import io.hamal.lib.kua.value.StringValue
import io.hamal.lib.kua.value.Value

@JvmInline
value class TableLength(val value: Int)

data class TableProxyContext(
    val index: Int,
    val bridge: Bridge
)

class TableProxy(
    ctx: TableProxyContext
) : Value, TableMap {

    override fun set(key: StringValue, value: StringValue) {
        TODO("Not yet implemented")
    }

    override fun set(key: String, value: StringValue): TableLength {
        bridge.pushString(key)
        bridge.pushString(value.value)
        return TableLength(bridge.tableSetRaw(index))
    }

    override fun set(key: String, value: String) {
        TODO("Not yet implemented")
    }

    private val index = ctx.index
    private val bridge = ctx.bridge
}
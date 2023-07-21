package io.hamal.lib.kua.table

import io.hamal.lib.kua.State

@JvmInline
value class TableLength(val value: Int)

data class TableProxyContext(
    val index: Int,
    val state: State
)

class TableProxy(
    ctx: TableProxyContext
) : TableMap, TableArray {

    override fun set(key: String, value: String): TableLength {
        state.pushString(key)
        state.pushString(value)
        return TableLength(bridge.tableSetRaw(index))
    }

    override fun set(key: String, value: Double): TableLength {
        bridge.pushString(key)
        bridge.pushNumber(value)
        return TableLength(bridge.tableSetRaw(index))
    }

    fun length(): TableLength = TableLength((bridge.tableGetLength(index)))

    private val index = ctx.index
    private val state = ctx.state
    private val bridge = ctx.state.bridge
}
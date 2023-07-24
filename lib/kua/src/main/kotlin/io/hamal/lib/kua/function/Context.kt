package io.hamal.lib.kua.function

import io.hamal.lib.kua.StackTop
import io.hamal.lib.kua.State
import io.hamal.lib.kua.table.TableArrayProxyValue
import io.hamal.lib.kua.table.TableMapProxyValue
import io.hamal.lib.kua.value.AnyValue
import io.hamal.lib.kua.value.TableValue
import io.hamal.lib.kua.value.ValueType


class FunctionContext(
    val state: State
) : State {
    override val bridge = state.bridge
    override val top: StackTop get() = state.top

    override fun isEmpty() = state.isEmpty()
    override fun isNotEmpty() = state.isNotEmpty()
    override fun setTop(idx: Int) = state.setTop(idx)
    override fun pushTop(idx: Int) = state.pushTop(idx)

    override fun type(idx: Int): ValueType = state.type(idx)
    override fun pushAny(value: AnyValue) = state.pushAny(value)
    override fun getAnyValue(idx: Int) = state.getAnyValue(idx)
    override fun pushBoolean(value: Boolean) = state.pushBoolean(value)
    override fun getBoolean(idx: Int) = state.getBoolean(idx)

    override fun getNumber(idx: Int) = state.getNumber(idx)
    override fun pushNumber(value: Double) = state.pushNumber(value)
    override fun getString(idx: Int) = state.getString(idx)
    override fun pushString(value: String) = state.pushString(value)
    override fun pushTable(value: TableValue) = state.pushTable(value)
    override fun pushTable(proxy: TableMapProxyValue) = state.pushTable(proxy)
    override fun pushTable(proxy: TableArrayProxyValue) = state.pushTable(proxy)
    override fun getTable(idx: Int) = state.getTable(idx)
    override fun getTableMapProxy(idx: Int) = state.getTableMapProxy(idx)
    override fun getTableArrayProxy(idx: Int) = state.getTableArrayProxy(idx)

    override fun setGlobal(name: String, value: TableMapProxyValue) = state.setGlobal(name, value)
    override fun setGlobal(name: String, value: TableArrayProxyValue) = state.setGlobal(name, value)

    override fun tableCreateMap(capacity: Int): TableMapProxyValue {
        TODO("Not yet implemented")
    }

    override fun tableCreateArray(capacity: Int): TableArrayProxyValue {
        TODO("Not yet implemented")
    }

    override fun tableInsert(idx: Int) = state.tableInsert(idx)
    override fun tableSetRaw(idx: Int) = state.tableSetRaw(idx)
    override fun tableSetRawIdx(stackIdx: Int, tableIdx: Int) = state.tableSetRawIdx(stackIdx, tableIdx)
    override fun tableGetRaw(idx: Int) = state.tableGetRaw(idx)
}

interface FunctionContextFactory {
    fun create(state: State): FunctionContext
}
package io.hamal.lib.kua.function

import io.hamal.lib.kua.StackTop
import io.hamal.lib.kua.State
import io.hamal.lib.kua.table.TableArrayValue
import io.hamal.lib.kua.table.TableMapValue
import io.hamal.lib.kua.value.AnyValue
import io.hamal.lib.kua.value.ErrorValue
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
    override fun pushNil() = state.pushNil()
    override fun pushAny(value: AnyValue) = state.pushAny(value)
    override fun getAnyValue(idx: Int) = state.getAnyValue(idx)
    override fun pushBoolean(value: Boolean) = state.pushBoolean(value)
    override fun getBoolean(idx: Int) = state.getBoolean(idx)
    override fun pushError(value: ErrorValue) = state.pushError(value)

    override fun getNumber(idx: Int) = state.getNumber(idx)
    override fun pushNumber(value: Double) = state.pushNumber(value)
    override fun getString(idx: Int) = state.getString(idx)
    override fun pushString(value: String) = state.pushString(value)
    override fun pushTable(value: TableValue) = state.pushTable(value)
    override fun pushTable(proxy: TableMapValue) = state.pushTable(proxy)
    override fun pushTable(proxy: TableArrayValue) = state.pushTable(proxy)
    override fun getTable(idx: Int) = state.getTable(idx)
    override fun getTableMap(idx: Int) = state.getTableMap(idx)
    override fun getTableArray(idx: Int) = state.getTableArray(idx)

    override fun setGlobal(name: String, value: TableMapValue) = state.setGlobal(name, value)
    override fun setGlobal(name: String, value: TableArrayValue) = state.setGlobal(name, value)
    override fun getGlobalTableMap(name: String): TableMapValue = state.getGlobalTableMap(name)

    override fun tableCreateMap(capacity: Int) = state.tableCreateMap(capacity)
    override fun tableCreateArray(capacity: Int) = state.tableCreateArray(capacity)

    override fun tableInsert(idx: Int) = state.tableInsert(idx)
    override fun tableSetRaw(idx: Int) = state.tableSetRaw(idx)
    override fun tableSetRawIdx(stackIdx: Int, tableIdx: Int) = state.tableSetRawIdx(stackIdx, tableIdx)
    override fun tableGetRaw(idx: Int) = state.tableGetRaw(idx)
    override fun tableGetRawIdx(stackIdx: Int, tableIdx: Int) = state.tableGetRawIdx(stackIdx, tableIdx)
}

interface FunctionContextFactory {
    fun create(state: State): FunctionContext
}
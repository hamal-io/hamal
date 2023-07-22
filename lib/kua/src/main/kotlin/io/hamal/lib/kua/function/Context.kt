package io.hamal.lib.kua.function

import io.hamal.lib.kua.StackTop
import io.hamal.lib.kua.State
import io.hamal.lib.kua.table.TableArray
import io.hamal.lib.kua.table.TableMap
import io.hamal.lib.kua.table.TableProxy
import io.hamal.lib.kua.table.TableProxyContext
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
    override fun getNumber(idx: Int) = state.getNumber(idx)
    override fun pushNumber(value: Double) = state.pushNumber(value)
    override fun getString(idx: Int) = state.getString(idx)
    override fun pushString(value: String) = state.pushString(value)

    override fun tableInsert(idx: Int) = state.tableInsert(idx)
    override fun tableSetRaw(idx: Int) = state.tableSetRaw(idx)
    override fun tableSetRawIdx(stackIdx: Int, tableIdx: Int) = state.tableSetRawIdx(stackIdx, tableIdx)
    override fun tableGetRaw(idx: Int) = state.tableGetRaw(idx)

    //FIXME move into state?!
    fun createArrayTable(capacity: Int): TableArray {
        bridge.tableCreate(capacity, 0)
        return TableProxy(
            TableProxyContext(
                bridge.top(),
                state
            )
        )
    }

    //FIXME move into state?!
    fun createMapTable(capacity: Int): TableMap {
        bridge.tableCreate(0, capacity)
        return TableProxy(
            TableProxyContext(
                bridge.top(),
                state
            )
        )
    }
}

interface FunctionContextFactory {
    fun create(state: State): FunctionContext
}
package io.hamal.lib.kua

import io.hamal.lib.kua.table.TableArrayProxyValue
import io.hamal.lib.kua.table.TableMapProxyValue
import io.hamal.lib.kua.value.AnyValue
import io.hamal.lib.kua.value.CodeValue
import io.hamal.lib.kua.value.TableValue
import io.hamal.lib.kua.value.ValueType
import io.hamal.lib.kua.value.ValueType.Companion.ValueType

interface SandboxFactory {
    fun create(): Sandbox
}

class Sandbox : State, AutoCloseable {
    override val bridge: Bridge = Bridge()
    override val top: StackTop get() = state.top

    val state = ClosableState(bridge)

    fun register(extension: Extension) = bridge.registerExtension(extension)

    fun runCode(code: CodeValue) = runCode(code.value)

    fun runCode(code: String) = bridge.runCode(code)

    override fun close() {
        state.close()
    }

    override fun isEmpty() = state.isEmpty()
    override fun isNotEmpty() = state.isNotEmpty()
    override fun setTop(idx: Int) = state.setTop(idx)
    override fun pushTop(idx: Int) = state.pushTop(idx)

    override fun type(idx: Int) = state.type(idx)
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

    override fun tableCreateMap(capacity: Int) = state.tableCreateMap(capacity)
    override fun tableCreateArray(capacity: Int) = state.tableCreateArray(capacity)

    override fun tableInsert(idx: Int) = state.tableInsert(idx)
    override fun tableSetRaw(idx: Int) = state.tableSetRaw(idx)
    override fun tableSetRawIdx(stackIdx: Int, tableIdx: Int) = state.tableSetRawIdx(stackIdx, tableIdx)
    override fun tableGetRaw(idx: Int): ValueType = ValueType(bridge.tableGetRaw(idx))
}

internal fun Bridge.runCode(code: String) {
    loadString(code)
    call(0, 0)
}

internal fun Bridge.registerExtension(module: Extension) {
    val funcs = module.functions

    tableCreate(0, funcs.size)
    val tblIdx = top()
    funcs.forEach { namedFunc ->
        pushFunctionValue(namedFunc.function)
        tabletSetField(tblIdx, namedFunc.name)
    }
//    if (global) {
//        rawGet(REGISTRYINDEX, LuaState.RIDX_GLOBALS)
    tableGetRawIdx(luaRegistryIndex(), 2)
//        pushValue(-2)
    pushTop(-2)
    tabletSetField(-2, module.name)
    pop(1)
//    }

}
package io.hamal.lib.kua

import io.hamal.lib.kua.function.NamedFunctionValue
import io.hamal.lib.kua.table.TableArrayProxyValue
import io.hamal.lib.kua.table.TableMapProxyValue
import io.hamal.lib.kua.value.AnyValue
import io.hamal.lib.kua.value.CodeValue
import io.hamal.lib.kua.value.ErrorValue
import io.hamal.lib.kua.value.TableValue

interface SandboxFactory {
    fun create(): Sandbox
}


class Sandbox : State, AutoCloseable {
    override val bridge: Bridge = Bridge()
    override val top: StackTop get() = state.top

    val state = ClosableState(bridge)

    fun register(extension: Extension) = state.registerExtension(extension)

    fun runCode(code: CodeValue) = runCode(code.value)

    fun runCode(code: String) = bridge.runCode(code)

    fun run(fn: (State) -> Unit) {
        fn(state)
    }

    override fun close() {
        state.close()
    }

    override fun isEmpty() = state.isEmpty()
    override fun isNotEmpty() = state.isNotEmpty()
    override fun setTop(idx: Int) = state.setTop(idx)
    override fun pushTop(idx: Int) = state.pushTop(idx)

    override fun type(idx: Int) = state.type(idx)
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
    override fun pushTable(proxy: TableMapProxyValue) = state.pushTable(proxy)
    override fun pushTable(proxy: TableArrayProxyValue) = state.pushTable(proxy)
    override fun getTable(idx: Int) = state.getTable(idx)
    override fun getTableMap(idx: Int) = state.getTableMap(idx)
    override fun getTableArray(idx: Int) = state.getTableArray(idx)

    override fun setGlobal(name: String, value: TableMapProxyValue) = state.setGlobal(name, value)
    override fun setGlobal(name: String, value: TableArrayProxyValue) = state.setGlobal(name, value)
    override fun getGlobalTableMap(name: String): TableMapProxyValue = state.getGlobalTableMap(name)

    override fun tableCreateMap(capacity: Int) = state.tableCreateMap(capacity)
    override fun tableCreateArray(capacity: Int) = state.tableCreateArray(capacity)

    override fun tableInsert(idx: Int) = state.tableInsert(idx)
    override fun tableSetRaw(idx: Int) = state.tableSetRaw(idx)
    override fun tableSetRawIdx(stackIdx: Int, tableIdx: Int) = state.tableSetRawIdx(stackIdx, tableIdx)
    override fun tableGetRaw(idx: Int) = state.tableGetRaw(idx)
    override fun tableGetRawIdx(stackIdx: Int, tableIdx: Int) = state.tableGetRawIdx(stackIdx, tableIdx)
}

internal fun Bridge.runCode(code: String) {
    loadString(code)
    call(0, 0)
}

internal fun State.registerExtension(extension: Extension) {
    val funcs = extension.functions

    val r = tableCreateMap(1)
    funcs.forEach { namedFunc ->
        bridge.pushFunctionValue(namedFunc.function)
        bridge.tabletSetField(r.index, namedFunc.name)
    }

    extension.extensions.forEach { nestedExt ->
        val nested = tableCreateMap(1)
        nestedExt.functions.forEach { namedFunc ->
            bridge.pushFunctionValue(namedFunc.function)
            bridge.tabletSetField(nested.index, namedFunc.name)
        }

        createConfig(nestedExt.config)
        bridge.tabletSetField(nested.index, "__config")

        bridge.tabletSetField(r.index, nestedExt.name)
    }

    createConfig(extension.config)
    bridge.tabletSetField(r.index, "__config")

    setGlobal(extension.name, r)
}

fun State.createConfig(config: ExtensionConfig): TableMapProxyValue {

    val result = tableCreateMap(1)

    val fns = listOf<NamedFunctionValue<*, *, *, *>>(
        NamedFunctionValue("get", ExtensionGetConfigFunction(config)),
        NamedFunctionValue("update", ExtensionUpdateConfigFunction(config))
    )

    fns.forEach { namedFunc ->
        bridge.pushFunctionValue(namedFunc.function)
        bridge.tabletSetField(result.index, namedFunc.name)
    }

    return result
}
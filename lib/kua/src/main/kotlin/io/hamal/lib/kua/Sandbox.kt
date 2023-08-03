package io.hamal.lib.kua

import io.hamal.lib.kua.builtin.Require
import io.hamal.lib.kua.extension.*
import io.hamal.lib.kua.function.FunctionValue
import io.hamal.lib.kua.table.TableArrayValue
import io.hamal.lib.kua.table.TableMapValue
import io.hamal.lib.kua.value.AnyValue
import io.hamal.lib.kua.value.CodeValue
import io.hamal.lib.kua.value.ErrorValue
import io.hamal.lib.kua.value.TableValue

interface SandboxFactory {
    fun create(): Sandbox
}


class Sandbox : State, AutoCloseable {

    override val bridge: Native = Native()
    override val top: StackTop get() = state.top

    val state = ClosableState(bridge)
    val registry: ExtensionRegistry = ExtensionRegistry(this)

    init {
        registerGlobalFunction("require", Require(registry))
        val classLoader = Sandbox::class.java.classLoader

        load(String(classLoader.getResource("std.lua").readBytes()))
    }

    fun register(extension: NativeExtension) = state.registerGlobalExtension(extension)

    fun load(code: CodeValue) = load(code.value)

    override fun load(code: String) = bridge.load(code)

    fun run(fn: (State) -> Unit) {
        fn(state)
    }

    fun register(extension: ScriptExtension) {
        registry.register(extension)
    }

    override fun close() {
        state.close()
    }

    override fun isEmpty() = state.isEmpty()
    override fun isNotEmpty() = state.isNotEmpty()
    override fun setTop(idx: Int) = state.setTop(idx)
    override fun absIndex(idx: Int) = state.absIndex(idx)

    override fun pushTop(idx: Int) = state.pushTop(idx)

    override fun type(idx: Int) = state.type(idx)
    override fun pushNil() = state.pushNil()
    override fun pushAny(value: AnyValue) = state.pushAny(value)
    override fun getAnyValue(idx: Int) = state.getAnyValue(idx)
    override fun pushBoolean(value: Boolean) = state.pushBoolean(value)
    override fun getBoolean(idx: Int) = state.getBoolean(idx)
    override fun pushError(value: ErrorValue) = state.pushError(value)
    override fun pushFunction(value: FunctionValue<*, *, *, *>) = state.pushFunction(value)

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

    override fun setGlobal(name: String, value: FunctionValue<*, *, *, *>) = state.setGlobal(name, value)
    override fun setGlobal(name: String, value: TableMapValue) = state.setGlobal(name, value)
    override fun setGlobal(name: String, value: TableArrayValue) = state.setGlobal(name, value)
    override fun getGlobalTableMap(name: String): TableMapValue = state.getGlobalTableMap(name)
    override fun unsetGlobal(name: String) = state.unsetGlobal(name)

    override fun tableCreateMap(capacity: Int) = state.tableCreateMap(capacity)
    override fun tableCreateArray(capacity: Int) = state.tableCreateArray(capacity)

    override fun tableAppend(idx: Int) = state.tableAppend(idx)
    override fun tableSetRaw(idx: Int) = state.tableSetRaw(idx)
    override fun tableSetRawIdx(stackIdx: Int, tableIdx: Int) = state.tableSetRawIdx(stackIdx, tableIdx)
    override fun tableGetRaw(idx: Int) = state.tableGetRaw(idx)
    override fun tableGetRawIdx(stackIdx: Int, tableIdx: Int) = state.tableGetRawIdx(stackIdx, tableIdx)
}

internal fun Native.load(code: String) {
    loadString(code)
    call(0, 0)
}

internal fun State.registerGlobalExtension(extension: NativeExtension) {
    val result = registerExtension(extension)
    setGlobal(extension.name, result)
}

fun State.registerExtension(extension: NativeExtension): TableMapValue {

    val r = tableCreateMap(1)
    extension.values
        .filter { entry -> entry.value is FunctionValue<*, *, *, *> }
        .forEach { (name, value) ->
            require(value is FunctionValue<*, *, *, *>)
            bridge.pushFunctionValue(value)
            bridge.tabletSetField(r.index, name)
        }

    createConfig(extension.config)
    bridge.tabletSetField(r.index, "__config")

    return r
}

fun State.createConfig(config: ExtensionConfig): TableMapValue {

    val result = tableCreateMap(1)

    val fns = mapOf(
        "get" to ExtensionGetConfigFunction(config),
        "update" to ExtensionUpdateConfigFunction(config)
    )

    fns.forEach { (name, value) ->
        bridge.pushFunctionValue(value)
        bridge.tabletSetField(result.index, name)
    }

    return result
}
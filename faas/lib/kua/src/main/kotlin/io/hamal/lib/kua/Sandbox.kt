package io.hamal.lib.kua

import io.hamal.lib.kua.builtin.Require
import io.hamal.lib.kua.extension.*
import io.hamal.lib.kua.function.FunctionType
import io.hamal.lib.kua.table.TableProxyArray
import io.hamal.lib.kua.table.TableProxyMap
import io.hamal.lib.kua.type.AnyType
import io.hamal.lib.kua.type.CodeType
import io.hamal.lib.kua.type.ErrorType

class Sandbox(
    val ctx: SandboxContext
) : State, AutoCloseable {

    override val native: Native = Native(this)
    override val top: StackTop get() = state.top
    override fun pop(len: Int) = state.pop(len)

    val state = ClosableState(native)
    val registry: ExtensionRegistry = ExtensionRegistry(this)

    init {
        registerGlobalFunction("require", Require(registry))

        val classLoader = Sandbox::class.java.classLoader
        load(String(classLoader.getResource("std.lua").readBytes()))
    }

    fun register(extension: NativeExtension) = state.registerGlobalExtension(extension)

    fun register(vararg factories: ExtensionFactory<*>): Sandbox {
        factories.map { it.create(this) }.forEach { extension ->
            check(extension is ScriptExtension)
            this.register(extension)
        }
        return this
    }

    fun load(code: CodeType) = load(code.value)

    override fun load(code: String) = native.load(code)

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
    override fun pushAny(value: AnyType) = state.pushAny(value)
    override fun getAny(idx: Int) = state.getAny(idx)
    override fun getArrayType(idx: Int) = state.getArrayType(idx)

    override fun pushBoolean(value: Boolean) = state.pushBoolean(value)
    override fun getBoolean(idx: Int) = state.getBoolean(idx)
    override fun pushError(value: ErrorType) = state.pushError(value)
    override fun pushFunction(value: FunctionType<*, *, *, *>) = state.pushFunction(value)

    override fun getNumber(idx: Int) = state.getNumber(idx)
    override fun pushNumber(value: Double) = state.pushNumber(value)
    override fun getString(idx: Int) = state.getString(idx)
    override fun pushString(value: String) = state.pushString(value)

    override fun pushTable(proxy: TableProxyMap) = state.pushTable(proxy)
    override fun pushTable(proxy: TableProxyArray) = state.pushTable(proxy)
    override fun getTableMapProxy(idx: Int) = state.getTableMapProxy(idx)
    override fun getTableArrayProxy(idx: Int) = state.getTableArrayProxy(idx)
    override fun getMapType(idx: Int) = state.getMapType(idx)

    override fun setGlobal(name: String, value: FunctionType<*, *, *, *>) = state.setGlobal(name, value)
    override fun setGlobal(name: String, value: TableProxyMap) = state.setGlobal(name, value)
    override fun setGlobal(name: String, value: TableProxyArray) = state.setGlobal(name, value)
    override fun getGlobalTableMap(name: String): TableProxyMap = state.getGlobalTableMap(name)
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

fun State.registerExtension(extension: NativeExtension): TableProxyMap {

    val r = tableCreateMap(1)
    extension.values
        .filter { entry -> entry.value is FunctionType<*, *, *, *> }
        .forEach { (name, value) ->
            require(value is FunctionType<*, *, *, *>)
            native.pushFunction(value)
            native.tabletSetField(r.index, name)
        }

    createConfig(extension.config)
    native.tabletSetField(r.index, "__config")

    return r
}

fun State.createConfig(config: ExtensionConfig): TableProxyMap {

    val result = tableCreateMap(1)

    val fns = mapOf(
        "get" to ExtensionGetConfigFunction(config),
        "update" to ExtensionUpdateConfigFunction(config)
    )

    fns.forEach { (name, value) ->
        native.pushFunction(value)
        native.tabletSetField(result.index, name)
    }

    return result
}
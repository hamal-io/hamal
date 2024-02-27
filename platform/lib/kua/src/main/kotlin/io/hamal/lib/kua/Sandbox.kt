package io.hamal.lib.kua

import io.hamal.lib.kua.builtin.Require
import io.hamal.lib.kua.builtin.RequirePlugin
import io.hamal.lib.kua.extend.RunnerRegistry
import io.hamal.lib.kua.extend.extension.RunnerExtension
import io.hamal.lib.kua.extend.extension.RunnerExtensionFactory
import io.hamal.lib.kua.extend.plugin.RunnerPlugin
import io.hamal.lib.kua.extend.plugin.RunnerPluginFactory
import io.hamal.lib.kua.type.*
import io.hamal.lib.kua.type.KuaError

class Sandbox(
    val ctx: SandboxContext,
    val state: CloseableState = CloseableStateImpl()
) : CloseableState {


    override fun decimalPush(value: KuaDecimal): StackTop = state.decimalPush(value)
    override fun decimalGet(idx: Int): KuaDecimal = state.decimalGet(idx)

    override fun errorPush(error: KuaError): StackTop = state.errorPush(error)
    override fun errorGet(idx: Int): KuaError = state.errorGet(idx)

    override fun topGet(): StackTop = state.topGet()


    // FIXME to remove

    override fun pop(len: Int) = state.pop(len)

    //    val state = CloseableStateImpl(native)
    val registry: RunnerRegistry = RunnerRegistry(this)

    init {
        registerGlobalFunction("require", Require(registry))
        registerGlobalFunction("require_plugin", RequirePlugin(registry))

        val classLoader = Sandbox::class.java.classLoader
        load(String(classLoader.getResource("std.lua").readBytes()))
    }

    fun load(code: KuaCode) = load(code.value)

    //    override fun load(code: String) = native.load(code)
    override fun load(code: String) = state.load(code)

    fun run(fn: (State) -> Unit) {
        fn(state)
    }

    fun register(extension: RunnerPlugin) {
        registry.register(extension)
    }

    fun registerPlugins(vararg factories: RunnerPluginFactory): Sandbox {
        factories.map { it.create(this) }.forEach { plugin ->
            this.register(plugin)
        }
        return this
    }

    fun registerExtensions(vararg factories: RunnerExtensionFactory): Sandbox {
        factories.map { it.create(this) }.forEach { extension ->
            this.register(extension)
        }
        return this
    }

    fun register(extension: RunnerExtension) {
        registry.register(extension)
    }


    override fun close() {
        state.close()
    }

    override fun isEmpty() = state.isEmpty()
    override fun isNotEmpty() = state.isNotEmpty()
    override fun topSet(idx: Int) = state.topSet(idx)
    override fun absIndex(idx: Int) = state.absIndex(idx)

    override fun pushTop(idx: Int) = state.pushTop(idx)

    override fun type(idx: Int) = state.type(idx)
    override fun pushNil() = state.pushNil()
    override fun pushAny(value: KuaAny) = state.pushAny(value)
    override fun getAny(idx: Int) = state.getAny(idx)


    override fun pushBoolean(value: Boolean) = state.pushBoolean(value)
    override fun getBoolean(idx: Int) = state.getBoolean(idx)
    override fun pushError(value: KuaError) = state.pushError(value)
    override fun pushFunction(value: KuaFunction<*, *, *, *>) = state.pushFunction(value)

    override fun getNumber(idx: Int) = state.getNumber(idx)
    override fun pushNumber(value: Double) = state.pushNumber(value)
    override fun getString(idx: Int) = state.getString(idx)
    override fun pushString(value: String) = state.pushString(value)

    override fun pushTable(proxy: KuaTable): StackTop = state.pushTable(proxy)

    override fun getTable(idx: Int): KuaTable = state.getTable(idx)
    override fun getTableArray(idx: Int) = state.getTableArray(idx)
    override fun getTableMap(idx: Int) = state.getTableMap(idx)

    override fun setGlobal(name: String, value: KuaFunction<*, *, *, *>) = state.setGlobal(name, value)
    override fun setGlobal(name: String, value: KuaTable) = state.setGlobal(name, value)
    override fun getGlobalKuaTableMap(name: String): KuaTable = state.getGlobalKuaTableMap(name)
    override fun unsetGlobal(name: String) = state.unsetGlobal(name)

    override fun tableCreateMap(capacity: Int) = state.tableCreateMap(capacity)
    override fun tableCreateArray(capacity: Int) = state.tableCreateArray(capacity)

    override fun tableAppend(idx: Int) = state.tableAppend(idx)
    override fun tableSetRaw(idx: Int) = state.tableSetRaw(idx)
    override fun tableSetRawIdx(stackIdx: Int, tableIdx: Int) = state.tableSetRawIdx(stackIdx, tableIdx)
    override fun tableGetRaw(idx: Int) = state.tableGetRaw(idx)
    override fun tableGetRawIdx(stackIdx: Int, tableIdx: Int) = state.tableGetRawIdx(stackIdx, tableIdx)
}
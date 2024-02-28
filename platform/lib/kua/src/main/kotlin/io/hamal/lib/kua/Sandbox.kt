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

    override fun absIndex(idx: Int) = state.absIndex(idx)

    override fun anyGet(idx: Int) = state.anyGet(idx)
    override fun anyPush(value: KuaAny) = state.anyPush(value)

    override fun booleanGet(idx: Int) = state.booleanGet(idx)
    override fun booleanPush(value: KuaBoolean) = state.booleanPush(value)

    override fun decimalGet(idx: Int): KuaDecimal = state.decimalGet(idx)
    override fun decimalPush(value: KuaDecimal): StackTop = state.decimalPush(value)

    override fun errorGet(idx: Int): KuaError = state.errorGet(idx)
    override fun errorPush(error: KuaError): StackTop = state.errorPush(error)

    override fun functionPush(value: KuaFunction<*, *, *, *>) = state.functionPush(value)

    override fun nilPush() = state.nilPush()

    override fun numberGet(idx: Int) = state.numberGet(idx)
    override fun numberPush(value: KuaNumber) = state.numberPush(value)

    override fun stringGet(idx: Int) = state.stringGet(idx)
    override fun stringPush(value: KuaString) = state.stringPush(value)

    override fun tableAppend(idx: Int) = state.tableAppend(idx)
    override fun tableCreate(arrayCount: Int, recordCount: Int) = state.tableCreate(arrayCount, recordCount)
    override fun tableGet(idx: Int): KuaTable = state.tableGet(idx)
    override fun tableFieldGet(idx: Int, key: KuaString) = state.tableFieldGet(idx, key)
    override fun tableFieldSet(idx: Int, key: KuaString) = state.tableFieldSet(idx, key)
    override fun tableLength(idx: Int) = state.tableLength(idx)
    override fun tablePush(proxy: KuaTable): StackTop = state.tablePush(proxy)
    override fun tableRawSet(idx: Int) = state.tableRawSet(idx)
    override fun tableRawSetIdx(stackIdx: Int, tableIdx: Int) = state.tableRawSetIdx(stackIdx, tableIdx)
    override fun tableRawGet(idx: Int) = state.tableRawGet(idx)
    override fun tableRawGetIdx(stackIdx: Int, tableIdx: Int) = state.tableRawGetIdx(stackIdx, tableIdx)

    override fun topGet(): StackTop = state.topGet()
    override fun topPop(len: Int) = state.topPop(len)
    override fun topPush(idx: Int) = state.topPush(idx)
    override fun topSet(idx: Int) = state.topSet(idx)

    override fun type(idx: Int) = state.type(idx)


    // FIXME to remove


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

    //    override fun isEmpty() = state.isEmpty()
//    override fun isNotEmpty() = state.isNotEmpty()




    override fun setGlobal(name: String, value: KuaFunction<*, *, *, *>) = state.setGlobal(name, value)
    override fun setGlobal(name: String, value: KuaTable) = state.setGlobal(name, value)
    override fun getGlobalKuaTableMap(name: String): KuaTable = state.getGlobalKuaTableMap(name)
    override fun unsetGlobal(name: String) = state.unsetGlobal(name)


}
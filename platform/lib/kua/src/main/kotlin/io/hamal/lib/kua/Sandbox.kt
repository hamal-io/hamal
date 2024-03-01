package io.hamal.lib.kua

import io.hamal.lib.kua.builtin.Require
import io.hamal.lib.kua.builtin.RequirePlugin
import io.hamal.lib.kua.extend.extension.RunnerExtension
import io.hamal.lib.kua.extend.extension.RunnerExtensionFactory
import io.hamal.lib.kua.extend.plugin.RunnerPlugin
import io.hamal.lib.kua.extend.plugin.RunnerPluginFactory
import io.hamal.lib.kua.type.*
import io.hamal.lib.kua.type.KuaError

class Sandbox(
    private val ctx: SandboxContext,
    private val state: CloseableState = CloseableStateImpl(),
    private val registry: SandboxRegistry = SandboxRegistryImpl(state)
) : CloseableState {

    override fun absIndex(idx: Int) = state.absIndex(idx)

    override fun anyGet(idx: Int) = state.anyGet(idx)
    override fun anyPush(value: KuaAny) = state.anyPush(value)

    override fun booleanGet(idx: Int) = state.booleanGet(idx)
    override fun booleanPush(value: KuaBoolean) = state.booleanPush(value)

    override fun codeLoad(code: KuaCode) = state.codeLoad(code)

    override fun decimalGet(idx: Int): KuaDecimal = state.decimalGet(idx)
    override fun decimalPush(value: KuaDecimal): StackTop = state.decimalPush(value)

    override fun errorGet(idx: Int): KuaError = state.errorGet(idx)
    override fun errorPush(error: KuaError): StackTop = state.errorPush(error)

    override fun functionPush(value: KuaFunction<*, *, *, *>) = state.functionPush(value)

    override fun globalGet(key: KuaString) = state.globalGet(key)
    override fun globalGetTable(key: KuaString) = state.globalGetTable(key)
    override fun globalSet(key: KuaString, value: KuaType) = state.globalSet(key, value)
    override fun globalUnset(key: KuaString) = state.globalUnset(key)

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

    init {
        registerGlobalFunction("require", Require(registry))
        registerGlobalFunction("require_plugin", RequirePlugin(registry))
        val classLoader = Sandbox::class.java.classLoader
        codeLoad(KuaCode(String(classLoader.getResource("std.lua").readBytes())))
    }


    fun register(plugin: RunnerPlugin) {
        registry.register(plugin)
    }

    fun registerPlugins(vararg factories: RunnerPluginFactory): Sandbox {
        factories.map { it.create(this) }.forEach(this::register)
        return this
    }

    fun registerExtensions(vararg factories: RunnerExtensionFactory): Sandbox {
        factories.map { it.create(this) }.forEach(this::register)
        return this
    }

    fun register(extension: RunnerExtension) {
        registry.register(extension)
    }

    override fun close() {
        state.close()
    }
}
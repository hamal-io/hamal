package io.hamal.lib.kua

import io.hamal.lib.common.value.*
import io.hamal.lib.kua.builtin.Require
import io.hamal.lib.kua.builtin.RequirePlugin
import io.hamal.lib.kua.extend.extension.RunnerExtension
import io.hamal.lib.kua.extend.extension.RunnerExtensionFactory
import io.hamal.lib.kua.extend.plugin.RunnerPlugin
import io.hamal.lib.kua.extend.plugin.RunnerPluginFactory
import io.hamal.lib.kua.value.KuaFunction
import io.hamal.lib.kua.value.KuaReference
import io.hamal.lib.kua.value.KuaTable
import io.hamal.lib.nodes.compiler.node.NodeCompilerRegistry

// FIXME super dirty temporary hack
val sandboxContextLocal = ThreadLocal<SandboxContext>()

class Sandbox(
    private val ctx: SandboxContext,
    private val state: CloseableState = CloseableStateImpl(),
    val registry: SandboxRegistry = SandboxRegistryImpl(state),
    val generatorNodeCompilerRegistry: NodeCompilerRegistry = NodeCompilerRegistry(listOf())
) : CloseableState {

    init {
        sandboxContextLocal.set(ctx)
    }

    override fun absIndex(idx: ValueNumber) = state.absIndex(idx)

    override fun get(idx: ValueNumber) = state.get(idx)
    override fun push(value: Value) = state.push(value)

    override fun booleanGet(idx: ValueNumber) = state.booleanGet(idx)
    override fun booleanPush(value: ValueBoolean) = state.booleanPush(value)

    override fun codeLoad(code: ValueCode) = state.codeLoad(code)
    override fun <T : Any> checkpoint(action: (State) -> T) = state.checkpoint(action)

    override fun decimalGet(idx: ValueNumber): ValueDecimal = state.decimalGet(idx)
    override fun decimalPush(value: ValueDecimal): StackTop = state.decimalPush(value)

    override fun errorGet(idx: ValueNumber): ValueError = state.errorGet(idx)
    override fun errorPush(error: ValueError): StackTop = state.errorPush(error)

    override fun functionPush(value: KuaFunction<*, *, *, *>) = state.functionPush(value)

    override fun globalGet(key: ValueString) = state.globalGet(key)
    override fun globalGetTable(key: ValueString) = state.globalGetTable(key)
    override fun globalSet(key: ValueString, value: Value) = state.globalSet(key, value)
    override fun globalUnset(key: ValueString) = state.globalUnset(key)

    override fun nilPush() = state.nilPush()

    override fun numberGet(idx: ValueNumber) = state.numberGet(idx)
    override fun numberPush(value: ValueNumber) = state.numberPush(value)

    override fun referenceAcquire() = state.referenceAcquire()
    override fun referencePush(reference: KuaReference) = state.referencePush(reference)
    override fun referenceRelease(reference: KuaReference) = state.referenceRelease(reference)

    override fun stringGet(idx: ValueNumber) = state.stringGet(idx)
    override fun stringPush(value: ValueString) = state.stringPush(value)

    override fun tableAppend(idx: ValueNumber) = state.tableAppend(idx)
    override fun tableCreate(arrayCount: ValueNumber, recordCount: ValueNumber) =
        state.tableCreate(arrayCount, recordCount)

    override fun tableGet(idx: ValueNumber): KuaTable = state.tableGet(idx)
    override fun tableFieldGet(idx: ValueNumber, key: ValueString) = state.tableFieldGet(idx, key)
    override fun tableFieldSet(idx: ValueNumber, key: ValueString) = state.tableFieldSet(idx, key)
    override fun tableLength(idx: ValueNumber) = state.tableLength(idx)
    override fun tableNext(idx: ValueNumber) = state.tableNext(idx)
    override fun tablePush(value: KuaTable): StackTop = state.tablePush(value)
    override fun tableRawSet(idx: ValueNumber) = state.tableRawSet(idx)
    override fun tableRawSetIdx(stackIdx: ValueNumber, tableIdx: ValueNumber) = state.tableRawSetIdx(stackIdx, tableIdx)
    override fun tableRawGet(idx: ValueNumber) = state.tableRawGet(idx)
    override fun tableRawGetIdx(stackIdx: ValueNumber, tableIdx: ValueNumber) = state.tableRawGetIdx(stackIdx, tableIdx)
    override fun tableSubTableGet(idx: ValueNumber, key: ValueString) = state.tableSubTableGet(idx, key)

    override fun topGet(): StackTop = state.topGet()
    override fun topPop(len: ValueNumber) = state.topPop(len)
    override fun topPush(idx: ValueNumber) = state.topPush(idx)
    override fun topSet(idx: ValueNumber) = state.topSet(idx)

    override fun type(idx: ValueNumber) = state.type(idx)

    init {
        registerGlobalFunction("require", Require(registry))
        registerGlobalFunction("require_plugin", RequirePlugin(registry))
        val classLoader = Sandbox::class.java.classLoader
        codeLoad(ValueCode(String(classLoader.getResource("std.lua").readBytes())))
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
        extension.nodeCompilers.forEach(generatorNodeCompilerRegistry::register)
    }

    override fun close() {
        state.close()
    }
}

fun Sandbox.registerGlobalFunction(name: String, function: KuaFunction<*, *, *, *>) {
    globalSet(ValueString(name), function)
}
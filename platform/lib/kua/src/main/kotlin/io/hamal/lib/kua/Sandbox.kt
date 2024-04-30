package io.hamal.lib.kua

import io.hamal.lib.kua.builtin.Require
import io.hamal.lib.kua.builtin.RequirePlugin
import io.hamal.lib.kua.extend.extension.RunnerExtension
import io.hamal.lib.kua.extend.extension.RunnerExtensionFactory
import io.hamal.lib.kua.extend.plugin.RunnerPlugin
import io.hamal.lib.kua.extend.plugin.RunnerPluginFactory
import io.hamal.lib.kua.type.*
import io.hamal.lib.kua.type.KuaError
import io.hamal.lib.nodes.generator.GeneratorRegistry
import io.hamal.lib.value.Value
import io.hamal.lib.value.ValueBoolean
import io.hamal.lib.value.ValueDecimal
import io.hamal.lib.value.ValueString

// FIXME super dirty temporary hack
val sandboxContextLocal = ThreadLocal<SandboxContext>()

class Sandbox(
    private val ctx: SandboxContext,
    private val state: CloseableState = CloseableStateImpl(),
    val registry: SandboxRegistry = SandboxRegistryImpl(state),
    val generatorRegistry: GeneratorRegistry = GeneratorRegistry(listOf())
) : CloseableState {

    init {
        sandboxContextLocal.set(ctx)
    }

    override fun absIndex(idx: KuaNumber) = state.absIndex(idx)

    override fun get(idx: KuaNumber) = state.get(idx)
    override fun push(value: Value) = state.push(value)

    override fun booleanGet(idx: KuaNumber) = state.booleanGet(idx)
    override fun booleanPush(value: ValueBoolean) = state.booleanPush(value)

    override fun codeLoad(code: KuaCode) = state.codeLoad(code)
    override fun <T : Any> checkpoint(action: (State) -> T) = state.checkpoint(action)

    override fun decimalGet(idx: KuaNumber): ValueDecimal = state.decimalGet(idx)
    override fun decimalPush(value: ValueDecimal): StackTop = state.decimalPush(value)

    override fun errorGet(idx: KuaNumber): KuaError = state.errorGet(idx)
    override fun errorPush(error: KuaError): StackTop = state.errorPush(error)

    override fun functionPush(value: KuaFunction<*, *, *, *>) = state.functionPush(value)

    override fun globalGet(key: ValueString) = state.globalGet(key)
    override fun globalGetTable(key: ValueString) = state.globalGetTable(key)
    override fun globalSet(key: ValueString, value: Value) = state.globalSet(key, value)
    override fun globalUnset(key: ValueString) = state.globalUnset(key)

    override fun nilPush() = state.nilPush()

    override fun numberGet(idx: KuaNumber) = state.numberGet(idx)
    override fun numberPush(value: KuaNumber) = state.numberPush(value)

    override fun referenceAcquire() = state.referenceAcquire()
    override fun referencePush(reference: KuaReference) = state.referencePush(reference)
    override fun referenceRelease(reference: KuaReference) = state.referenceRelease(reference)

    override fun stringGet(idx: KuaNumber) = state.stringGet(idx)
    override fun stringPush(value: ValueString) = state.stringPush(value)

    override fun tableAppend(idx: KuaNumber) = state.tableAppend(idx)
    override fun tableCreate(arrayCount: KuaNumber, recordCount: KuaNumber) = state.tableCreate(arrayCount, recordCount)
    override fun tableGet(idx: KuaNumber): KuaTable = state.tableGet(idx)
    override fun tableFieldGet(idx: KuaNumber, key: ValueString) = state.tableFieldGet(idx, key)
    override fun tableFieldSet(idx: KuaNumber, key: ValueString) = state.tableFieldSet(idx, key)
    override fun tableLength(idx: KuaNumber) = state.tableLength(idx)
    override fun tableNext(idx: KuaNumber) = state.tableNext(idx)
    override fun tablePush(value: KuaTable): StackTop = state.tablePush(value)
    override fun tableRawSet(idx: KuaNumber) = state.tableRawSet(idx)
    override fun tableRawSetIdx(stackIdx: KuaNumber, tableIdx: KuaNumber) = state.tableRawSetIdx(stackIdx, tableIdx)
    override fun tableRawGet(idx: KuaNumber) = state.tableRawGet(idx)
    override fun tableRawGetIdx(stackIdx: KuaNumber, tableIdx: KuaNumber) = state.tableRawGetIdx(stackIdx, tableIdx)
    override fun tableSubTableGet(idx: KuaNumber, key: ValueString) = state.tableSubTableGet(idx, key)

    override fun topGet(): StackTop = state.topGet()
    override fun topPop(len: KuaNumber) = state.topPop(len)
    override fun topPush(idx: KuaNumber) = state.topPush(idx)
    override fun topSet(idx: KuaNumber) = state.topSet(idx)

    override fun type(idx: KuaNumber) = state.type(idx)

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
        extension.generators.forEach(generatorRegistry::register)
    }

    override fun close() {
        state.close()
    }
}
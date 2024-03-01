package io.hamal.lib.kua

import io.hamal.lib.kua.extend.extension.RunnerExtension
import io.hamal.lib.kua.extend.plugin.RunnerPlugin
import io.hamal.lib.kua.type.KuaCode
import io.hamal.lib.kua.type.KuaFunction
import io.hamal.lib.kua.type.KuaString
import io.hamal.lib.kua.type.KuaTable

interface SandboxRegistry {
    fun register(plugin: RunnerPlugin)
    fun register(extension: RunnerExtension)

    fun loadPluginFactory(name: String): KuaTable
    fun loadExtensionFactory(name: String): KuaTable
}

internal class SandboxRegistryImpl(
    private val state: State
) : SandboxRegistry {

    override fun register(plugin: RunnerPlugin) {
        plugins[plugin.name] = plugin
        // FIXME load the factory
        loadPluginFactory(plugin.name)
    }

    override fun register(extension: RunnerExtension) {
        extensions[extension.name] = extension
        // FIXME load the factory
        loadExtensionFactory(extension.name)
    }

    override fun loadPluginFactory(name: String): KuaTable {
        val extension = plugins[name]!!
        val internals = extension.internals
        val internalTable = state.tableCreate(0, internals.size)

        internals.forEach { entry ->
            val fn = entry.value
            require(fn is KuaFunction<*, *, *, *>)
            internalTable[entry.key] = fn
        }

        state.globalSet(KuaString("_internal"), internalTable)
        state.codeLoad(plugins[name]!!.factoryCode)
        state.codeLoad(KuaCode("_factory = plugin_factory_create()()"))

        state.globalUnset(KuaString("_internal"))

        // FIXME cache factory so that it does not have to be loaded over and over again
        val factory = state.globalGetTable(KuaString("_factory"))
        factories[name] = factory

        state.globalUnset(KuaString("plugin"))
        return factory
    }

    override fun loadExtensionFactory(name: String): KuaTable {
        val extension = extensions[name]!!

        state.codeLoad(extension.factoryCode)
        state.codeLoad(KuaCode("_factory = extension_factory_create()()"))

        // FIXME cache factory so that it does not have to be loaded over and over again
        val factory = state.globalGetTable(KuaString("_factory"))
        factories[name] = factory

        state.globalUnset(KuaString("extension"))
        return factory
    }

    private val plugins = mutableMapOf<String, RunnerPlugin>()
    private val extensions = mutableMapOf<String, RunnerExtension>()
    private val factories = mutableMapOf<String, KuaTable>()

}
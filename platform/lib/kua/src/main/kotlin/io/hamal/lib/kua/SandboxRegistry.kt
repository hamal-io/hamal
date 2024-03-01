package io.hamal.lib.kua

import io.hamal.lib.kua.extend.extension.RunnerExtension
import io.hamal.lib.kua.extend.plugin.RunnerPlugin
import io.hamal.lib.kua.type.KuaCode
import io.hamal.lib.kua.type.KuaString
import io.hamal.lib.kua.type.KuaTable

interface SandboxRegistry {
    fun register(plugin: RunnerPlugin)
    fun register(extension: RunnerExtension)

    fun loadPluginFactory(name: String)
    fun invokeExtensionFactory(name: String): KuaTable
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
        invokeExtensionFactory(extension.name)
    }

    override fun loadPluginFactory(name: String) {
        val extension = plugins[name]!!

        state.globalSet(KuaString("_internal"), state.tableCreate(extension.internals))
        state.codeLoad(plugins[name]!!.factoryCode)
        state.codeLoad(KuaCode("_instance = plugin_create(_internal)"))

        state.globalGetTable(KuaString("_instance")).also {
            // clean up
            state.globalUnset(KuaString("_internal"))
            state.globalUnset(KuaString("_instance"))
        }
    }

    override fun invokeExtensionFactory(name: String): KuaTable {
        val extension = extensions[name]!!

        state.codeLoad(extension.factoryCode)
        state.codeLoad(KuaCode("_factory = extension_factory_create()()"))

        // FIXME cache factory so that it does not have to be loaded over and over again
        val factory = state.globalGetTable(KuaString("_factory"))
//        factories[name] = factory

        state.globalUnset(KuaString("extension"))
        return factory
    }

    private val plugins = mutableMapOf<String, RunnerPlugin>()
    private val extensions = mutableMapOf<String, RunnerExtension>()
}
package io.hamal.lib.kua.extension

import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extension.plugin.RunnerPluginExtension
import io.hamal.lib.kua.extension.script.RunnerScriptExtension
import io.hamal.lib.kua.function.FunctionType
import io.hamal.lib.kua.table.TableProxyMap

class RunnerExtensionRegistry(val sb: Sandbox) {

    val state = sb.state
    val pluginExtensions = mutableMapOf<String, RunnerPluginExtension>()
    val scriptExtensions = mutableMapOf<String, RunnerScriptExtension>()
    val factories = mutableMapOf<String, TableProxyMap>()

    fun isScript(name: String) = scriptExtensions.keys.contains(name)

    fun isPlugin(name: String) = pluginExtensions.keys.contains(name)

    fun register(extension: RunnerPluginExtension) {
        pluginExtensions[extension.name] = extension
        // FIXME load the factory
        loadPluginExtensionFactory(extension.name)
    }

    fun register(extension: RunnerScriptExtension) {
        scriptExtensions[extension.name] = extension
        // FIXME load the factory
        loadScriptExtensionFactory(extension.name)
    }

    fun loadPluginExtensionFactory(name: String): TableProxyMap {
        val extension = pluginExtensions[name]!!
        val internals = extension.internals
        val internalTable = state.tableCreateMap(internals.size)

        internals.forEach { entry ->
            val fn = entry.value
            require(fn is FunctionType<*, *, *, *>)
            internalTable[entry.key] = fn
        }

        sb.setGlobal("_internal", internalTable)

        state.load(pluginExtensions[name]!!.factoryCode)
        state.load("_factory = extension()")

        sb.unsetGlobal("_internal")

        // FIXME cache factory so that it does not have to be loaded over and over again
        val factory = state.getGlobalTableMap("_factory")
        factories[name] = factory

        state.unsetGlobal("extension")
        return factory
    }

    fun loadScriptExtensionFactory(name: String): TableProxyMap {
        val extension = scriptExtensions[name]!!

        state.load(extension.factoryCode)
        state.load("_factory = extension()")

        // FIXME cache factory so that it does not have to be loaded over and over again
        val factory = state.getGlobalTableMap("_factory")
        factories[name] = factory

        state.unsetGlobal("extension")
        return factory
    }

}
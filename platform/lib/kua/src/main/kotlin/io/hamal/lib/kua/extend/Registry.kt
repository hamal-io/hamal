package io.hamal.lib.kua.extend

import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extend.extension.RunnerExtension
import io.hamal.lib.kua.extend.plugin.RunnerPlugin
import io.hamal.lib.kua.table.TableProxyMap
import io.hamal.lib.kua.type.KuaFunction

class RunnerRegistry(val sb: Sandbox) {

    val state = sb.state
    val plugins = mutableMapOf<String, RunnerPlugin>()
    val extensions = mutableMapOf<String, RunnerExtension>()
    val factories = mutableMapOf<String, TableProxyMap>()

    fun isScript(name: String) = extensions.keys.contains(name)

    fun isPlugin(name: String) = plugins.keys.contains(name)

    fun register(plugin: RunnerPlugin) {
        plugins[plugin.name] = plugin
        // FIXME load the factory
        loadPluginFactory(plugin.name)
    }

    fun register(extension: RunnerExtension) {
        extensions[extension.name] = extension
        // FIXME load the factory
        loadExtensionFactory(extension.name)
    }

    fun loadPluginFactory(name: String): TableProxyMap {
        val extension = plugins[name]!!
        val internals = extension.internals
        val internalTable = state.tableCreateMap(internals.size)

        internals.forEach { entry ->
            val fn = entry.value
            require(fn is KuaFunction<*, *, *, *>)
            internalTable[entry.key] = fn
        }

        sb.setGlobal("_internal", internalTable)

        state.load(plugins[name]!!.factoryCode)
        state.load("_factory = plugin()")

        sb.unsetGlobal("_internal")

        // FIXME cache factory so that it does not have to be loaded over and over again
        val factory = state.getGlobalTableMap("_factory")
        factories[name] = factory

        state.unsetGlobal("plugin")
        return factory
    }

    fun loadExtensionFactory(name: String): TableProxyMap {
        val extension = extensions[name]!!

        state.load(extension.factoryCode)
        state.load("_factory = extension()")

        // FIXME cache factory so that it does not have to be loaded over and over again
        val factory = state.getGlobalTableMap("_factory")
        factories[name] = factory

        state.unsetGlobal("extension")
        return factory
    }

}
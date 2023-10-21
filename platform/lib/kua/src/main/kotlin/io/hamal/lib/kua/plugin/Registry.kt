package io.hamal.lib.kua.plugin

import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.function.FunctionType
import io.hamal.lib.kua.plugin.capability.Capability
import io.hamal.lib.kua.plugin.extension.Extension
import io.hamal.lib.kua.table.TableProxyMap

class PluginRegistry(val sb: Sandbox) {

    val state = sb.state
    val capabilities = mutableMapOf<String, Capability>()
    val extensions = mutableMapOf<String, Extension>()
    val factories = mutableMapOf<String, TableProxyMap>()

    fun register(capability: Capability) {
        capabilities[capability.name] = capability
        // FIXME load the factory
        loadCapabilityFactory(capability.name)
    }

    fun register(extension: Extension) {
        extensions[extension.name] = extension
        // FIXME load the factory
        loadExtensionFactory(extension.name)
    }

    fun loadCapabilityFactory(name: String): TableProxyMap {
        val capability = capabilities[name]!!
        val internals = capability.internals
        val internalTable = state.tableCreateMap(internals.size)

        internals.forEach { entry ->
            val fn = entry.value
            require(fn is FunctionType<*, *, *, *>)
            internalTable[entry.key] = fn
        }

        sb.setGlobal("_internal", internalTable)

        state.load(capabilities[name]!!.factoryCode)
        state.load("_factory = plugin()")
        sb.unsetGlobal("_internal")

        // FIXME cache factory so that it does not have to be loaded over and over again
        val factory = state.getGlobalTableMap("_factory")
        factories[name] = factory

        state.unsetGlobal("extension")
        state.unsetGlobal("plugin")
        return factory
    }

    fun loadExtensionFactory(name: String): TableProxyMap {
        val extension = extensions[name]!!

        state.load(extension.factoryCode)
        state.load("_factory = plugin()")

        // FIXME cache factory so that it does not have to be loaded over and over again
        val factory = state.getGlobalTableMap("_factory")
        factories[name] = factory

        state.unsetGlobal("extension")
        state.unsetGlobal("plugin")
        return factory
    }


}
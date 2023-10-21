package io.hamal.lib.kua.capability

import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.function.FunctionType
import io.hamal.lib.kua.table.TableProxyMap


class ExtensionRegistry(
    val sb: Sandbox
) {

    val state = sb.state
    val extensions = mutableMapOf<String, Capability>()
    val extensionFactories = mutableMapOf<String, TableProxyMap>()

    fun register(extension: Capability) {
        extensions[extension.name] = extension

        // FIXME load the factory
        loadFactory(extension.name)

    }

    fun loadFactory(name: String): TableProxyMap {
        val extension = extensions[name]!!
        val internals = extension.internals
        val internalTable = state.tableCreateMap(internals.size)

        internals.forEach { entry ->
            val fn = entry.value
            require(fn is FunctionType<*, *, *, *>)
            internalTable[entry.key] = fn
        }

        sb.setGlobal("_internal", internalTable)

        state.load(extensions[name]!!.factoryCode)
        state.load("_factory = create_capability_factory()")
        sb.unsetGlobal("_internal")

        // FIXME cache factory so that it does not have to be loaded over and over again
        val factory = state.getGlobalTableMap("_factory")
        extensionFactories[name] = factory

        state.unsetGlobal("extension")
        state.unsetGlobal("create_capability_factory")
        return factory
    }
}
package io.hamal.lib.kua.extension

import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.function.FunctionValue
import io.hamal.lib.kua.table.TableMapValue


class ExtensionRegistry(
    val sb: Sandbox
) {

    val state = sb.state
    val extensions = mutableMapOf<String, NewExt>()
    val extensionFactories = mutableMapOf<String, TableMapValue>()

    fun register(extension: NewExt) {
        extensions[extension.name] = extension

        // FIXME load the factory
        loadFactory(extension.name)

    }

    fun loadFactory(name: String): TableMapValue {
        val extension = extensions[name]!!
        val internals = extension.internals
        val internalTable = state.tableCreateMap(internals.size)

        internals.forEach { entry ->
            val fn = entry.value
            require(fn is FunctionValue<*, *, *, *>)
            internalTable[entry.key] = fn
        }

        sb.setGlobal("_internal", internalTable)

        state.load(extensions[name]!!.init)
        state.load("_factory = create_extension_factory()")
        sb.unsetGlobal("_internal")

        // FIXME cache factory so that it does not have to be loaded over and over again
        val factory = state.getGlobalTableMap("_factory")
        extensionFactories[name] = factory

        state.unsetGlobal("extension")
        state.unsetGlobal("create_extension_factory")
        return factory
    }
}
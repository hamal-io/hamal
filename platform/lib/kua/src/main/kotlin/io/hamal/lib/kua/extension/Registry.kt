package io.hamal.lib.kua.extension

import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.extension.safe.RunnerSafeExtension
import io.hamal.lib.kua.extension.unsafe.RunnerUnsafeExtension
import io.hamal.lib.kua.function.FunctionType
import io.hamal.lib.kua.table.TableProxyMap

class RunnerExtensionRegistry(val sb: Sandbox) {

    val state = sb.state
    val unsafeExtensions = mutableMapOf<String, RunnerUnsafeExtension>()
    val safeExtensions = mutableMapOf<String, RunnerSafeExtension>()
    val factories = mutableMapOf<String, TableProxyMap>()

    fun isSafeExtension(name: String) = safeExtensions.keys.contains(name)

    fun isUnsafeExtension(name: String) = unsafeExtensions.keys.contains(name)

    fun register(extension: RunnerUnsafeExtension) {
        unsafeExtensions[extension.name] = extension
        // FIXME load the factory
        loadUnsafeExtensionFactory(extension.name)
    }

    fun register(extension: RunnerSafeExtension) {
        safeExtensions[extension.name] = extension
        // FIXME load the factory
        loadSafeExtensionFactory(extension.name)
    }

    fun loadUnsafeExtensionFactory(name: String): TableProxyMap {
        val extension = unsafeExtensions[name]!!
        val internals = extension.internals
        val internalTable = state.tableCreateMap(internals.size)

        internals.forEach { entry ->
            val fn = entry.value
            require(fn is FunctionType<*, *, *, *>)
            internalTable[entry.key] = fn
        }

        sb.setGlobal("_internal", internalTable)

        state.load(unsafeExtensions[name]!!.factoryCode)
        state.load("_factory = extension()")
        sb.unsetGlobal("_internal")

        // FIXME cache factory so that it does not have to be loaded over and over again
        val factory = state.getGlobalTableMap("_factory")
        factories[name] = factory

        state.unsetGlobal("extension")
        return factory
    }

    fun loadSafeExtensionFactory(name: String): TableProxyMap {
        val extension = safeExtensions[name]!!

        state.load(extension.factoryCode)
        state.load("_factory = extension()")

        // FIXME cache factory so that it does not have to be loaded over and over again
        val factory = state.getGlobalTableMap("_factory")
        factories[name] = factory

        state.unsetGlobal("extension")
        return factory
    }

}
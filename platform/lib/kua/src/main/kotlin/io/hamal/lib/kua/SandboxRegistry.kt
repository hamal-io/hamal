package io.hamal.lib.kua

import io.hamal.lib.kua.extend.extension.RunnerExtension
import io.hamal.lib.kua.extend.plugin.RunnerPlugin
import io.hamal.lib.kua.type.KuaCode
import io.hamal.lib.kua.type.KuaString

interface SandboxRegistry {
    fun register(plugin: RunnerPlugin)
    fun register(extension: RunnerExtension)

    fun pluginPush(name: String)
    fun extensionPush(name: String)
}

internal class SandboxRegistryImpl(
    private val state: State
) : SandboxRegistry {

    override fun register(plugin: RunnerPlugin) {
        plugins[plugin.name] = plugin
        pluginPush(plugin.name)
    }

    override fun register(extension: RunnerExtension) {
        extensions[extension.name] = extension
        extensionPush(extension.name)
    }

    override fun pluginPush(name: String) {
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

    override fun extensionPush(name: String) {
        val extension = extensions[name]!!

        state.codeLoad(extension.factoryCode)
        state.codeLoad(KuaCode("_instance = extension_create()"))
        state.globalGetTable(KuaString("_instance")).also {
            // clean up
            state.globalUnset(KuaString("_instance"))
        }
    }

    private val plugins = mutableMapOf<String, RunnerPlugin>()
    private val extensions = mutableMapOf<String, RunnerExtension>()
}
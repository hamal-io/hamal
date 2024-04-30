package io.hamal.lib.kua

import io.hamal.lib.kua.extend.extension.RunnerExtension
import io.hamal.lib.kua.extend.plugin.RunnerPlugin
import io.hamal.lib.kua.type.KuaCode
import io.hamal.lib.value.ValueString

interface SandboxRegistry {
    fun register(plugin: RunnerPlugin)
    fun register(extension: RunnerExtension)

    fun pluginPush(name: ValueString)
    fun extensionPush(name: ValueString)
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

    override fun pluginPush(name: ValueString) {
        val extension = plugins[name]!!

        state.globalSet(ValueString("_internal"), state.tableCreate(extension.internals))
        state.codeLoad(plugins[name]!!.factoryCode)
        state.codeLoad(KuaCode("_instance = plugin_create(_internal)"))

        state.globalGetTable(ValueString("_instance")).also {
            // clean up
            state.globalUnset(ValueString("_internal"))
            state.globalUnset(ValueString("_instance"))
        }
    }

    override fun extensionPush(name: ValueString) {
        val extension = extensions[name]!!

        state.codeLoad(extension.factoryCode)
        state.codeLoad(KuaCode("_instance = extension_create()"))
        state.globalGetTable(ValueString("_instance")).also {
            // clean up
            state.globalUnset(ValueString("_instance"))
        }
    }

    private val plugins = mutableMapOf<ValueString, RunnerPlugin>()
    private val extensions = mutableMapOf<ValueString, RunnerExtension>()
}
package io.hamal.lib.kua

import io.hamal.lib.common.value.ValueCode
import io.hamal.lib.common.value.ValueString
import io.hamal.lib.domain.vo.ExtensionName
import io.hamal.lib.kua.extend.extension.RunnerExtension
import io.hamal.lib.kua.extend.plugin.RunnerPlugin

interface SandboxRegistry {
    fun register(plugin: RunnerPlugin)
    fun register(extension: RunnerExtension)

    fun pluginPush(name: ValueString)
    fun push(name: ExtensionName)
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
        push(extension.name)
    }

    override fun pluginPush(name: ValueString) {
        val extension = plugins[name] ?: throw NoSuchElementException("$name not found in registry")

        state.globalSet(ValueString("_internal"), state.tableCreate(extension.internals))
        state.codeLoad(plugins[name]!!.factoryCode)
        state.codeLoad(ValueCode("_instance = plugin_create(_internal)"))

        state.globalGetTable(ValueString("_instance")).also {
            // clean up
            state.globalUnset(ValueString("_internal"))
            state.globalUnset(ValueString("_instance"))
        }
    }

    override fun push(name: ExtensionName) {
        val extension = extensions[name] ?: throw NoSuchElementException("$name not found in registry")

        state.codeLoad(extension.factoryCode)
        state.codeLoad(ValueCode("_instance = extension_create()"))
        state.globalGetTable(ValueString("_instance")).also {
            // clean up
            state.globalUnset(ValueString("_instance"))
        }
    }

    private val plugins = mutableMapOf<ValueString, RunnerPlugin>()
    private val extensions = mutableMapOf<ExtensionName, RunnerExtension>()
}
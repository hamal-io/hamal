package io.hamal.lib.kua.extend.plugin

import io.hamal.lib.common.value.Value
import io.hamal.lib.common.value.ValueCode
import io.hamal.lib.common.value.ValueString
import io.hamal.lib.kua.Sandbox

fun interface RunnerPluginFactory {
    fun create(sandbox: Sandbox): RunnerPlugin
}

class RunnerPlugin(
    val name: ValueString,
    val factoryCode: ValueCode = loadFactoryCodeFromResources(name),
    val internals: Map<ValueString, Value> = mapOf()
) {

    companion object {
        @JvmStatic
        private fun loadFactoryCodeFromResources(extensionName: ValueString): ValueCode {
            val path = "${extensionName.stringValue.replace(".", "/")}/plugin.lua"
            val classLoader = this::class.java.classLoader
            val resource = classLoader.getResource(path)
            checkNotNull(resource) { "Unable to load: $path" }
            return ValueCode(String(resource.readBytes()))
        }
    }
}



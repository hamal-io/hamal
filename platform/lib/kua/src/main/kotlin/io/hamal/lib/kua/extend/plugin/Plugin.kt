package io.hamal.lib.kua.extend.plugin

import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.type.KuaCode
import io.hamal.lib.value.Value
import io.hamal.lib.value.ValueString

interface RunnerPluginFactory {
    fun create(sandbox: Sandbox): RunnerPlugin
}

class RunnerPlugin(
    val name: ValueString,
    val factoryCode: KuaCode = loadFactoryCodeFromResources(name),
    val internals: Map<ValueString, Value> = mapOf()
) {

    companion object {
        @JvmStatic
        private fun loadFactoryCodeFromResources(extensionName: ValueString): KuaCode {
            val path = "${extensionName.stringValue.replace(".", "/")}/plugin.lua"
            val classLoader = this::class.java.classLoader
            val resource = classLoader.getResource(path)
            checkNotNull(resource) { "Unable to load: $path" }
            return KuaCode(String(resource.readBytes()))
        }
    }
}



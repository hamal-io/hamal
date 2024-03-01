package io.hamal.lib.kua.extend.plugin

import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.type.KuaCode
import io.hamal.lib.kua.type.KuaString
import io.hamal.lib.kua.type.KuaType

interface RunnerPluginFactory {
    fun create(sandbox: Sandbox): RunnerPlugin
}

class RunnerPlugin(
    val name: String,
    val factoryCode: KuaCode = loadFactoryCodeFromResources(name),
    val internals: Map<KuaString, KuaType> = mapOf()
) {

    companion object {
        @JvmStatic
        private fun loadFactoryCodeFromResources(extensionName: String): KuaCode {
            val path = "${extensionName.replace(".", "/")}/plugin.lua"
            val classLoader = this::class.java.classLoader
            val resource = classLoader.getResource(path)
            checkNotNull(resource) { "Unable to load: $path" }
            return KuaCode(String(resource.readBytes()))
        }
    }
}



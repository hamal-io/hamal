package io.hamal.lib.kua.extend.plugin

import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.type.Type

interface RunnerPluginFactory {
    fun create(sandbox: Sandbox): RunnerPlugin
}

class RunnerPlugin(
    val name: String,
    val factoryCode: String = loadFactoryCodeFromResources(name),
    val internals: Map<String, Type> = mapOf()
) {

    companion object {
        @JvmStatic
        private fun loadFactoryCodeFromResources(extensionName: String): String { // FIXME extend name VO
            val path = "${extensionName.replace(".", "/")}/plugin.lua"
            val classLoader = this::class.java.classLoader
            val resource = classLoader.getResource(path)
            checkNotNull(resource) { "Unable to load: $path" }
            return String(resource.readBytes())
        }
    }
}



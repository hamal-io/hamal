package io.hamal.lib.kua.extend.extension

import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.type.KuaCode
import io.hamal.lib.kua.type.KuaString
import io.hamal.lib.nodes.NodeExtension


interface RunnerExtensionFactory {
    fun create(sandbox: Sandbox): RunnerExtension
}

class RunnerExtension(
    val name: KuaString,
    val factoryCode: KuaCode = loadFactoryCodeFromResources(name),
    val nodes: List<NodeExtension> = listOf()
) {
    companion object {
        @JvmStatic
        private fun loadFactoryCodeFromResources(extensionName: KuaString): KuaCode {
            val path = "${extensionName.stringValue.replace(".", "/")}/extension.lua"
            val classLoader = this::class.java.classLoader
            val resource = classLoader.getResource(path)
            checkNotNull(resource) { "Unable to load: $path" }
            return KuaCode(String(resource.readBytes()))
        }
    }
}



package io.hamal.lib.kua.extend.extension

import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.type.KuaCode
import io.hamal.lib.nodes.NodeExtension
import io.hamal.lib.nodes.generator.Generator
import io.hamal.lib.value.ValueString


interface RunnerExtensionFactory {
    fun create(sandbox: Sandbox): RunnerExtension
}

class RunnerExtension(
    val name: ValueString,
    val factoryCode: KuaCode = loadFactoryCodeFromResources(name),
    val nodes: List<NodeExtension> = listOf(),
    val generators: List<Generator> = listOf()
) {
    companion object {
        @JvmStatic
        private fun loadFactoryCodeFromResources(extensionName: ValueString): KuaCode {
            val path = "${extensionName.stringValue.replace(".", "/")}/extension.lua"
            val classLoader = this::class.java.classLoader
            val resource = classLoader.getResource(path)
            checkNotNull(resource) { "Unable to load: $path" }
            return KuaCode(String(resource.readBytes()))
        }
    }
}



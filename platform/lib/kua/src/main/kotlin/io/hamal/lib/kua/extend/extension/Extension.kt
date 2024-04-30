package io.hamal.lib.kua.extend.extension

import io.hamal.lib.kua.Sandbox
import io.hamal.lib.value.ValueCode
import io.hamal.lib.nodes.NodeExtension
import io.hamal.lib.nodes.generator.Generator
import io.hamal.lib.value.ValueString


interface RunnerExtensionFactory {
    fun create(sandbox: Sandbox): RunnerExtension
}

class RunnerExtension(
    val name: ValueString,
    val factoryCode: ValueCode = loadFactoryCodeFromResources(name),
    val nodes: List<NodeExtension> = listOf(),
    val generators: List<Generator> = listOf()
) {
    companion object {
        @JvmStatic
        private fun loadFactoryCodeFromResources(extensionName: ValueString): ValueCode {
            val path = "${extensionName.stringValue.replace(".", "/")}/extension.lua"
            val classLoader = this::class.java.classLoader
            val resource = classLoader.getResource(path)
            checkNotNull(resource) { "Unable to load: $path" }
            return ValueCode(String(resource.readBytes()))
        }
    }
}



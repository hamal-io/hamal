package io.hamal.lib.kua.extend.extension

import io.hamal.lib.common.value.ValueCode
import io.hamal.lib.domain.vo.ExtensionFile
import io.hamal.lib.domain.vo.ExtensionFile.Companion.ExtensionFileName
import io.hamal.lib.domain.vo.ExtensionName
import io.hamal.lib.kua.Sandbox
import io.hamal.lib.nodes.compiler.node.AbstractNode


fun interface RunnerExtensionFactory {
    fun create(sandbox: Sandbox): RunnerExtension
}

class RunnerExtension(
    val name: ExtensionName,
    val files: List<ExtensionFile> = listOf(ExtensionFileName("extension.lua")),
    val factoryCode: ValueCode = loadFactoryCodeFromResources(name, files),
//    val nodes: List<TemplateNode> = listOf(),
    val nodeCompilers: List<AbstractNode> = listOf(),
    val dependencies: List<RunnerExtensionFactory> = listOf()
) {
    companion object {
        @JvmStatic
        private fun loadFactoryCodeFromResources(name: ExtensionName, files: List<ExtensionFile>): ValueCode {
            val classLoader = this::class.java.classLoader
            return ValueCode(
                files.joinToString("\n") {
                    val path = "${name.stringValue.replace(".", "/")}/extension.lua"
                    val resource = classLoader.getResource(path)
                    checkNotNull(resource) { "Unable to load: $path" }
                    String(resource.readBytes())
                }
            )
        }
    }
}



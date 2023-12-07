package io.hamal.lib.kua.extend.extension

import io.hamal.lib.kua.Sandbox


interface RunnerExtensionFactory {
    fun create(sandbox: Sandbox): RunnerExtension
}

class RunnerExtension(
    val name: String,
    val factoryCode: String = loadFactoryCodeFromResources(name)
) {
    companion object {
        @JvmStatic
        private fun loadFactoryCodeFromResources(extensionName: String): String { // FIXME extend name VO
            val path = "${extensionName.replace(".", "/")}/extension.lua"
            val classLoader = this::class.java.classLoader
            val resource = classLoader.getResource(path)
            checkNotNull(resource) { "Unable to load: $path" }
            return String(resource.readBytes())
        }
    }
}



package io.hamal.lib.kua.extend.extension

import io.hamal.lib.kua.Sandbox
import io.hamal.lib.kua.type.KuaCode


interface RunnerExtensionFactory {
    fun create(sandbox: Sandbox): RunnerExtension
}

class RunnerExtension(
    val name: String,
    val factoryCode: KuaCode = loadFactoryCodeFromResources(name)
) {
    companion object {
        @JvmStatic
        private fun loadFactoryCodeFromResources(extensionName: String): KuaCode {
            val path = "${extensionName.replace(".", "/")}/extension.lua"
            val classLoader = this::class.java.classLoader
            val resource = classLoader.getResource(path)
            checkNotNull(resource) { "Unable to load: $path" }
            return KuaCode(String(resource.readBytes()))
        }
    }
}



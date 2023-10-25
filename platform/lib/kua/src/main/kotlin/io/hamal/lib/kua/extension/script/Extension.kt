package io.hamal.lib.kua.extension.script

import io.hamal.lib.kua.extension.*

interface RunnerScriptExtensionFactory : RunnerExtensionFactory<RunnerScriptExtension>

class RunnerScriptExtension(
    override val name: String,
    override val factoryCode: String = loadFactoryCodeFromResources(name),
    val config: ExtensionConfig = ExtensionConfig(mutableMapOf()),
) : RunnerExtension {

    companion object {
        @JvmStatic
        private fun loadFactoryCodeFromResources(extensionName: String): String { // FIXME extension name VO
            val path = "${extensionName.replace(".", "/")}/extension.lua"
            val classLoader = this::class.java.classLoader
            val resource = classLoader.getResource(path)
            checkNotNull(resource) { "Unable to load: $path" }
            return String(resource.readBytes())
        }
    }

    fun configGetFunction() = ExtensionConfigGetFunction(config)

    fun configUpdateFunction() = ExtensioConfignUpdateFunction(config)
}



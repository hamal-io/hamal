package io.hamal.lib.kua.extension.safe

import io.hamal.lib.kua.extension.RunnerExtension
import io.hamal.lib.kua.extension.RunnerExtensionFactory

interface RunnerSafeExtensionFactory : RunnerExtensionFactory<RunnerSafeExtension>

class RunnerSafeExtension(
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

    fun getConfigFunction() = ExtensionGetConfigFunction(config)

    fun updateConfigFunction() = ExtensionUpdateConfigFunction(config)
}



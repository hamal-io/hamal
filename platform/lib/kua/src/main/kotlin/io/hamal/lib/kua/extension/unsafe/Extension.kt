package io.hamal.lib.kua.extension.unsafe

import io.hamal.lib.kua.extension.*
import io.hamal.lib.kua.type.Type

interface RunnerUnsafeExtensionFactory : RunnerExtensionFactory<RunnerUnsafeExtension>

class RunnerUnsafeExtension(
    override val name: String,
    override val factoryCode: String = loadFactoryCodeFromResources(name),
    val internals: Map<String, Type> = mapOf(),
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


